package ru.yandex.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.client.OrderClient;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

public class TestOrderList {

    private OrderClient orderClient;
    int randomID = (int) (Math.random() * 30);

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Check the order list can be received") //Тут ломается кодировка в allure, если на русском
    public void checkGettingListOfOrdersTest() {

        ValidatableResponse response = orderClient.getOrderList();
        int statusCode = response.extract().statusCode();
        List<Map<String, Object>> orders = response.extract().jsonPath().getList("orders");

        assertEquals("Некорректный код статуса", 200, statusCode);
        assertThat("Список заказов пуст", orders, hasSize(30));
        assertThat("Некорректный ID заказа", orders.get(randomID).get("id"), notNullValue());
    }

}
