package ru.yandex.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.praktikum.client.CourierClient;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.CourierCredentials;
import ru.yandex.praktikum.model.CourierGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TestCourierLogin {

    private CourierClient courierClient;
    private int courierId;
    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(
                new RequestLoggingFilter(), new ResponseLoggingFilter(),
                new AllureRestAssured()
        );
    }

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @After
    public void clearData() {
        courierClient.delete(courierId);
    }

    @Test
    @DisplayName(" урьер может авторизоватьс€")
    public void courierCanLogin() {
        Courier courier = CourierGenerator.getRandom();
        courierClient.create(courier);

        courierClient.login(CourierCredentials.from(courier))
                .assertThat()
                .statusCode(SC_OK);
    }

    @Test
    @DisplayName("Ќевозможно залогинитьс€ курьером без парол€")
    public void courierNotLoggedInWithoutPassword() {
        Courier courier = CourierGenerator.getRandom();

        courierClient.create(courier);
        courier.setPassword("");
        courierClient.login(CourierCredentials.from(courier))
                .assertThat()
                .statusCode(SC_BAD_REQUEST);
    }
    @Test
    @DisplayName("Ќевозможно залогинитьс€ курьером без логина")
    public void courierNotLoggedInWithoutLogin() {
        Courier courier = CourierGenerator.getRandom();

        courierClient.create(courier);
        courier.setLogin("");
        courierClient.login(CourierCredentials.from(courier))
                .assertThat()
                .statusCode(SC_BAD_REQUEST);
    }
    @Test
    @DisplayName("Ќевозможно залогинитьс€ курьером с неправильным паролем")
    public void courierNotLoggedInWrongPassword() {
        Courier courier = CourierGenerator.getRandom();

        courierClient.create(courier);
        courier.setPassword(RandomStringUtils.randomAlphabetic(10));
        courierClient.login(CourierCredentials.from(courier))
                .assertThat()
                .statusCode(SC_NOT_FOUND);
    }
    @Test
    @DisplayName("Ќевозможно залогинитьс€ курьером с неправильным логином")
    public void courierNotLoggedInWrongLogin() {
        Courier courier = CourierGenerator.getRandom();

        courierClient.create(courier);
        courier.setLogin(RandomStringUtils.randomAlphabetic(10));
        courierClient.login(CourierCredentials.from(courier))
                .assertThat()
                .statusCode(SC_NOT_FOUND);
    }
    @Test
    @DisplayName("Ќевозможно залогинитьс€ курьером с несуществующей учетной записью") //‘актически этот случай уже проверен предыдущими тестами
    public void courierNotLoggedInIfNotExist() {
        Courier courier = CourierGenerator.getRandom();
        courierClient.login(CourierCredentials.from(courier))
                .assertThat()
                .statusCode(SC_NOT_FOUND);
    }
    @Test
    @DisplayName("”спешный логин возвращает id")
    public void courierCanBeCreatedWithValidData() {
        Courier courier = CourierGenerator.getRandom();

        courierClient.create(courier);

        courierId = courierClient.login(CourierCredentials.from(courier))
                .assertThat()
                .body("id", notNullValue())
                .extract().path("id");

    }
}
