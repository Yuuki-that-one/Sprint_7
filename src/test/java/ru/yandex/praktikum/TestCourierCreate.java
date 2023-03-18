package ru.yandex.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.praktikum.client.CourierClient;
import ru.yandex.praktikum.model.Courier;
import ru.yandex.praktikum.model.CourierCredentials;
import ru.yandex.praktikum.model.CourierGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;


public class TestCourierCreate {

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

//    @Test
//    @DisplayName("создание курьера с валидными параметрами")
//    public void courierCanBeCreatedWithValidData1() {
//        Courier courier = CourierGenerator.getRandom();
//
//        ValidatableResponse createResponse = courierClient.create(courier);
//        int statusCode = createResponse.extract().statusCode();
//        boolean isCourierCreated = createResponse.extract().path("ok");
//
//        assertEquals("Status code is incorrect", HTTP_CREATED, statusCode);
//        assertTrue("Courier is not created", isCourierCreated);
//
//        ValidatableResponse loginResponse = courierClient.login(CourierCredentials.from(courier));
//        courierId = loginResponse.extract().path("id");
//
//        assertTrue("Courier ID is not created", courierId != 0);
//    }

    @Test
    @DisplayName("Создание курьера с валидными параметрами")
    public void courierCanBeCreatedWithValidData() {
        Courier courier = CourierGenerator.getRandom();

        courierClient.create(courier)
                .assertThat()
                .statusCode(SC_CREATED)
                .and()
                .assertThat()
                .body("ok", is(true));

        courierId = courierClient.login(CourierCredentials.from(courier))
                .assertThat()
                .body("id", notNullValue())
                .extract().path("id");

    }
    @Test
    @DisplayName("Невозможно создать двух одинаковых курьеров")
    public void courierNotCreatedTheSameTwice() {
        Courier courier = CourierGenerator.getRandom();
        courierClient.create(courier)
                .assertThat()
                .statusCode(SC_CREATED);

        courierClient.create(courier)
                .assertThat()
                .statusCode(SC_CONFLICT);
    }
    @Test
    @DisplayName("Невозможно создать курьера без логина")
    public void courierNotCreatedWithoutLogin() {

        Courier courier = CourierGenerator.getRandom();
        courier.setLogin(null);

        courierClient.create(courier)
                .assertThat()
                .statusCode(SC_BAD_REQUEST);
    }
    @Test
    @DisplayName("Невозможно создать курьера без пароля")
    public void courierNotCreatedWithoutPassword() {
        Courier courier = CourierGenerator.getRandom();
        courier.setPassword(null);
        courierClient.create(courier)
                .assertThat()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Невозможно создать курьера с уже существующим логином")
    public void courierNotCreatedWithSameLogin() {
        Courier courier1 = CourierGenerator.getRandom();
        Courier courier2 = CourierGenerator.getRandom();
        courier2.setLogin(courier1.getLogin());

        courierClient.create(courier1)
                .assertThat()
                .statusCode(SC_CREATED);


        courierClient.create(courier2)
                .assertThat()
                .statusCode(SC_CONFLICT);
    }

}
