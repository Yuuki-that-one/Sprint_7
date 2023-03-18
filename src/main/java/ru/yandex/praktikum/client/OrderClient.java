package ru.yandex.praktikum.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.praktikum.client.base.ScooterRestClient;
import ru.yandex.praktikum.model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient extends ScooterRestClient {

    private static final String ORDER_PATH = "/api/v1/orders";
    private static final String GET_ORDER_PATH = "/api/v1/orders/track?t=";

    @Step("Create order")
    public ValidatableResponse createOrder(Order order){
        return given()
                .spec(getBaseReqSpec())
                .body(order)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Receive order")
    public ValidatableResponse getOrderList() {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .get(ORDER_PATH)
                .then();
    }

    @Step("Receive order id")
    public ValidatableResponse getOrderInfo(int trackID) {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .get(GET_ORDER_PATH + trackID)
                .then();
    }

    @Step("Cancel order")
    public ValidatableResponse cancel(int trackId) {
        return given()
                .spec(getBaseReqSpec())
                .body(trackId)
                .when()
                .put(ORDER_PATH + "cancel/")
                .then();
    }

}