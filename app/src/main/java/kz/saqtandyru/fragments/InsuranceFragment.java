package kz.saqtandyru.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import kz.saqtandyru.R;
import kz.saqtandyru.adapters.DocumentsListAdapter;
import kz.saqtandyru.database.DBHelper;
import kz.saqtandyru.interfaces.Documents;
import kz.saqtandyru.model.Document;

public class InsuranceFragment extends Fragment implements Documents, DocumentsListAdapter.OnDocumentListener {

    private ArrayList<Document> documents = new ArrayList<>();
    private RecyclerView recyclerView;
    private DocumentsListAdapter documentsListAdapter;
    private FloatingActionButton fab;
    private DBHelper dbHelper;
    private ArrayList<String> allCars;
    private Spinner chooseCarSpinner;
    private ArrayAdapter<String> spinnerAdapter;
    private int spinnerSelectedItemPosition;

    public static InsuranceFragment newInstance(String text) {

        InsuranceFragment f = new InsuranceFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DBHelper(getContext());
        documents = dbHelper.getInsurances();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recyclerview_layout, container, false);
        documentsListAdapter = new DocumentsListAdapter(getContext(), R.layout.item_document, this, documents);
        recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(documentsListAdapter);

        fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(v -> newDocumentDialog());

        return rootView;
    }

    public void newDocumentDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        View view = layoutInflaterAndroid.inflate(R.layout.dialog_add_insurance, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getContext());
        alertDialogBuilderUserInput.setView(view);

        final EditText policy = view.findViewById(R.id.insurance_policy_input);
        final EditText additionalInfo = view.findViewById(R.id.insurance_additional_info_input);
        final EditText dateFrom = view.findViewById(R.id.insurance_date_input);
        final EditText dateTo = view.findViewById(R.id.insurance_expiry_date_input);

        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateString = getResources().getString(R.string.date_hint, dateFormat.format(currentDate));
        dateFrom.setText(dateString);
        calendar.add(Calendar.YEAR, 1);
        Date nextYear = calendar.getTime();
        String nextYearString = getResources().getString(R.string.date_hint, dateFormat.format(nextYear));
        dateTo.setText(nextYearString);

        allCars = dbHelper.getCarsNames();

        spinnerAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, allCars);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        chooseCarSpinner = view.findViewById(R.id.insurance_choose_car_spinner);
        chooseCarSpinner.setAdapter(spinnerAdapter);
        chooseCarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerSelectedItemPosition = chooseCarSpinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(getContext(), "Сізге көлік таңдау керек!", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("ҚОСУ", (dialogBox, id) -> {
                    if (TextUtils.isEmpty(policy.getText().toString()) ||
                            TextUtils.isEmpty(additionalInfo.getText().toString()) ||
                            TextUtils.isEmpty(dateFrom.getText().toString()) ||
                            TextUtils.isEmpty(dateTo.getText().toString())) {
                        Toast.makeText(getContext(), "Сізге әрбір өрісті толтыруыңыз керек!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!allCars.isEmpty()) {
                        Document mDocument = new Document(
                                dbHelper.getCar(spinnerSelectedItemPosition + 1).getMarka() + " " + dbHelper.getCar(spinnerSelectedItemPosition + 1).getModel(),
                                "Сақтандыру полис нөмірі: " + policy.getText().toString(),
                                additionalInfo.getText().toString(),
                                dateFrom.getText().toString(),
                                dateTo.getText().toString()
                        );

                        dbHelper.insertInsurance(spinnerSelectedItemPosition + 1, mDocument);
                        documents.add(mDocument);
                        documentsListAdapter.notifyDataSetChanged();

                        Toast.makeText(getContext(), "Жаңа құжат қосылды", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Алдымен жаңа көлік қосыңыз", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("БАС ТАРТУ",
                        (dialogBox, id) -> dialogBox.cancel());

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();
    }

    @Override
    public void editDocumentDialog(int id) {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        View view = layoutInflaterAndroid.inflate(R.layout.dialog_add_insurance, null);

        final EditText policy = view.findViewById(R.id.insurance_policy_input);
        final EditText additionalInfo = view.findViewById(R.id.insurance_additional_info_input);
        final EditText dateFrom = view.findViewById(R.id.insurance_date_input);
        final EditText dateTo = view.findViewById(R.id.insurance_expiry_date_input);

        final Document currentDocument = dbHelper.getInsurance(id);

        allCars = new ArrayList<>();
        allCars.add(currentDocument.getAuto().toUpperCase());
        spinnerAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, allCars);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseCarSpinner = view.findViewById(R.id.insurance_choose_car_spinner);
        chooseCarSpinner.setAdapter(spinnerAdapter);

        policy.setText(currentDocument.getInfo());
        additionalInfo.setText(currentDocument.getAdditionalInfo());
        dateFrom.setText(currentDocument.getDate());
        dateTo.setText(currentDocument.getExpiryDate());

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        alertDialogBuilderUserInput.setView(view);

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("САҚТАУ", (dialogBox, dialogID) -> {
                            if (TextUtils.isEmpty(policy.getText().toString()) ||
                                    TextUtils.isEmpty(additionalInfo.getText().toString()) ||
                                    TextUtils.isEmpty(dateFrom.getText().toString()) ||
                                    TextUtils.isEmpty(dateTo.getText().toString())) {
                                Toast.makeText(getContext(), "Сізге әрбір өрісті толтыруыңыз керек!", Toast.LENGTH_SHORT).show();
                            } else {

                                Document updatedDoc = new Document(
                                        currentDocument.getAuto(),
                                        policy.getText().toString(),
                                        additionalInfo.getText().toString(),
                                        dateFrom.getText().toString(),
                                        dateTo.getText().toString()
                                );

                                dbHelper.updateInsurance(id, updatedDoc);
                                documents.set(id - 1, updatedDoc);
                                documentsListAdapter.notifyDataSetChanged();
                                dbHelper.close();

                                Toast.makeText(getContext(), "Көліктің сақтандыру деректері сақталды" + currentDocument.getAuto(), Toast.LENGTH_LONG).show();
                            }
                        }
                )
                .setNegativeButton("БАС ТАРТУ",
                        (dialogBox, id1) -> dialogBox.cancel());
        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();
    }

    @Override
    public void onDocumentClick(int position) {
        editDocumentDialog(position + 1);
    }
}
