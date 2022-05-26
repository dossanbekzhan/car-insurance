package kz.saqtandyru.utils;

import java.util.Arrays;
import java.util.List;

import kz.saqtandyru.model.Company;

public class Utils {
    public static List<Company> getAllCompanies() {
        return Arrays.asList(
                new Company(1L, "Freedom Finance", "Freedom Finance компаниясы өзіңізді және өзіңіздің жеке-мүлік заттарыңды сақтандыру полисін ұсынады"),
                new Company(2L, "Halyk Insurance", "Halyk Insurance компаниясы өзіңізді және өзіңіздің жеке-мүлік заттарыңды сақтандыру полисін ұсынады"),
                new Company(3L, "Jusan Garant", "Jusan Garant компаниясы өзіңізді және өзіңіздің жеке-мүлік заттарыңды сақтандыру полисін ұсынады"),
                new Company(4L, "Виктория СП", "Виктория СП компаниясы өзіңізді және өзіңіздің жеке-мүлік заттарыңды сақтандыру полисін ұсынады")
        );
    }

}
