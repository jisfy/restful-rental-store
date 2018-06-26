package com.chompchompfig.store.tools;

import java.text.MessageFormat;

public class JsonPathTools {

    public String jsonPathForFilmNameInList(int indexOfFilmInList) {
        return jsonPathForFilmPropertyInList(indexOfFilmInList, "name");
    }

    public String jsonPathForFilmCategoryInList(int indexOfFilmInList) {
        return jsonPathForFilmPropertyInList(indexOfFilmInList, "category");
    }

    public String jsonPathForFilmPropertyInList(int indexOfFilmInList, String propertyName) {
        String jsonPathForFilmPropertyInList = MessageFormat.format(
                "$._embedded[''ex:films''][{0, number, integer}].{1}", indexOfFilmInList, propertyName);
        return jsonPathForFilmPropertyInList;
    }

    public String jsonPathForCustomerPropertyInList(int indexOfCustomerInList, String propertyName) {
        String jsonPathForCustomerPropertyInList = MessageFormat.format(
                "$._embedded[''ex:customers''][{0, number, integer}].{1}", indexOfCustomerInList, propertyName);
        return jsonPathForCustomerPropertyInList;
    }

    public String jsonPathForPaymentPropertyInList(int indexOfPaymentInList, String propertyName) {
        String jsonPathForPaymentPropertyInList = MessageFormat.format(
                "$._embedded[''ex:payments''][{0, number, integer}].{1}", indexOfPaymentInList, propertyName);
        return jsonPathForPaymentPropertyInList;
    }

    public String jsonPathForRentalPropertyInList(int indexOfRentalInList, String propertyName) {
        String jsonPathForRentalPropertyInList = MessageFormat.format(
                "$._embedded[''ex:rentals''][{0, number, integer}].{1}", indexOfRentalInList, propertyName);
        return jsonPathForRentalPropertyInList;
    }

    public String jsonPathForProperty(String propertyName) {
        String jsonPathForProperty = MessageFormat.format("$.{0}", propertyName);
        return jsonPathForProperty;
    }
}
