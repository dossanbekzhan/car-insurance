package kz.saqtandyru.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import kz.saqtandyru.R;
import kz.saqtandyru.adapters.ExpandableListAdapter;
import kz.saqtandyru.database.DBHelper;
import kz.saqtandyru.interfaces.Cars;
import kz.saqtandyru.model.Car;
import kz.saqtandyru.model.CarPart;

public class ReplacementsFragment extends Fragment implements Cars {

    private ArrayList<Car> carData;
    private ArrayList<String> allCars;
    private HashMap<Car, List<CarPart>> carParts;
    private ExpandableListAdapter adapter;
    private DBHelper dbHelper;
    private Spinner chooseCarSpinner;
    private int spinnerSelectedItemPosition;
    private Cars mInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInterface = this;
        dbHelper = new DBHelper(getContext());

        carData = dbHelper.getAllCars();

        if (carData != null) {
            carParts = new HashMap<>();

            for (int i = 0; i < carData.size(); i++) {
                ArrayList<CarPart> partsList = dbHelper.getSpecficCarParts(i + 1);
                carParts.put(carData.get(i), partsList);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_replacements, container, false);
        final ExpandableListView expandableListView = rootView.findViewById(R.id.expandableList);
        adapter = new ExpandableListAdapter(getContext(), mInterface, dbHelper, R.layout.expandablelist_group, R.layout.expandablelist_child, carData, carParts);
        expandableListView.setAdapter(adapter);

        final FloatingActionButton fabAddPart = rootView.findViewById(R.id.add_new_part_fab_id);
        fabAddPart.setOnClickListener(view -> addDialog());

        return rootView;
    }

    public void addDialog() {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        View view = layoutInflaterAndroid.inflate(R.layout.dialog_add_part, null);

        final AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        alertDialogBuilderUserInput.setView(view);

        final EditText partNew = view.findViewById(R.id.dialog_new_part_input);
        final EditText partAdditionalInfo = view.findViewById(R.id.dialog_additional_info_input);
        final EditText partReplacementDate = view.findViewById(R.id.dialog_replacement_date_input);
        final EditText partPrice = view.findViewById(R.id.dialog_part_price_input);

        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dateString = getResources().getString(R.string.date_hint, dateFormat.format(currentDate));
        partReplacementDate.setText(dateString);

        allCars = dbHelper.getCarsNames();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_spinner_item, allCars);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        chooseCarSpinner = view.findViewById(R.id.choose_car_spinner_id);
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
                    if (TextUtils.isEmpty(partNew.getText().toString()) ||
                            TextUtils.isEmpty(partReplacementDate.getText().toString()) ||
                            TextUtils.isEmpty(partPrice.getText().toString()) ||
                            chooseCarSpinner.getSelectedItem() == null) {
                        Toast.makeText(getContext(), "Әр өрісті толтырып, көлікті таңдау керек!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!allCars.isEmpty()) {
                            dbHelper.insertPart(
                                    spinnerSelectedItemPosition + 1,
                                    partNew.getText().toString(),
                                    partAdditionalInfo.getText().toString(),
                                    partReplacementDate.getText().toString(),
                                    partPrice.getText().toString()
                            );

                            ArrayList<CarPart> partsList = dbHelper.getSpecficCarParts(spinnerSelectedItemPosition + 1);
                            carParts.put(carData.get(spinnerSelectedItemPosition), partsList);
                            adapter.notifyDataSetChanged();
                        }
                        Toast.makeText(getContext(), "Көлікке жаңа бөлік қосылды " +
                                allCars.get(spinnerSelectedItemPosition), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("БАС ТАРТУ",
                        (dialogBox, id) -> dialogBox.cancel());

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();
    }

    public void editDialog(final int id) {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getContext());
        View view = layoutInflaterAndroid.inflate(R.layout.dialog_add_car, null);

        final EditText carName = view.findViewById(R.id.car_marka_input);
        final EditText carModel = view.findViewById(R.id.car_model_input);
        final EditText carYear = view.findViewById(R.id.car_rok_input);
        final EditText carCapacity = view.findViewById(R.id.car_pojemnosc_input);
        final EditText carPower = view.findViewById(R.id.car_moc_input);
        final CheckBox checkBox = view.findViewById(R.id.main_car_checkbox);

        final Car currentCar = dbHelper.getCar(id);
        carName.setText(currentCar.getMarka());
        carModel.setText(currentCar.getModel());
        carYear.setText(currentCar.getRok_produkcji());
        carCapacity.setText(currentCar.getPojemnosc());
        carPower.setText(currentCar.getMoc());

        if (currentCar.isMainCar() > 0) {
            checkBox.setChecked(true);
        }

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        alertDialogBuilderUserInput.setView(view);

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("САҚТАУ", (dialogBox, dialogID) -> {

                    if (TextUtils.isEmpty(carName.getText().toString()) ||
                            TextUtils.isEmpty(carModel.getText().toString()) ||
                            TextUtils.isEmpty(carYear.getText().toString()) ||
                            TextUtils.isEmpty(carCapacity.getText().toString()) ||
                            TextUtils.isEmpty(carPower.getText().toString())) {
                        Toast.makeText(getContext(), "Сіз әрбір өрісті толтыруыңыз керек!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (checkBox.isChecked()) {
                            Car updatedCar = new Car(
                                    carName.getText().toString(),
                                    carModel.getText().toString(),
                                    carYear.getText().toString(),
                                    carCapacity.getText().toString(),
                                    carPower.getText().toString(),
                                    R.drawable.ic_car,
                                    1
                            );
                            dbHelper.updateCar(id, updatedCar);
                            dbHelper.setMainCar(id);
                            carData.set(id - 1, updatedCar);
                            carParts.put(carData.get(id - 1), dbHelper.getSpecficCarParts(id));

                            Toast.makeText(getContext(), "Көлік деректері өзгертілді" +
                                    currentCar.getMarka() + " (" + currentCar.getModel() +
                                    ") және негізгі көлік ретінде орнатыңыз", Toast.LENGTH_LONG).show();
                        } else {
                            Car updatedCar = new Car(
                                    carName.getText().toString(),
                                    carModel.getText().toString(),
                                    carYear.getText().toString(),
                                    carCapacity.getText().toString(),
                                    carPower.getText().toString(),
                                    R.drawable.ic_car,
                                    0
                            );

                            dbHelper.updateCar(id, updatedCar);
                            carData.set(id - 1, updatedCar);
                            carParts.put(carData.get(id - 1), dbHelper.getSpecficCarParts(id));

                            Toast.makeText(getContext(), "Көлік деректері өзгертілді" + currentCar.getMarka() + " (" + currentCar.getModel() + ")", Toast.LENGTH_LONG).show();
                        }

                        adapter.notifyDataSetChanged();
                        dbHelper.close();
                    }
                })
                .setNegativeButton("БАС ТАРТУ",
                        (dialogBox, id1) -> dialogBox.cancel())
                .setNeutralButton("ЖОЮ",
                        (dialogInterface, i) -> {
                            dbHelper.deletePart(id);
                            dbHelper.deleteCar(id);
                            adapter.notifyDataSetChanged();

                            Toast.makeText(getContext(), "Көлік алынып тасталды" +
                                    currentCar.getMarka() + " (" +
                                    currentCar.getModel() + ")", Toast.LENGTH_LONG).show();
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();
    }
}
