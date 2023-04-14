package subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철역 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationAcceptanceTest {
    private static final String basePath = "/stations";

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStationTest() {
        // Given
        String stationName = "강남역";

        // When
        ExtractableResponse<Response> response = createStation(stationName);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // Then
        List<String> stationNames = getStations();
        assertThat(stationNames).contains(stationName);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStationsTest() {
        // Given
        createStation("강남역");
        createStation("서울역");

        // When
        List<String> stationNames = getStations();

        // Then
        assertThat(stationNames.size()).isEqualTo(2);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStationTest() {
        // Given
        String stationName = "강남역";
        int stationId = createStation(stationName)
                .body()
                .jsonPath()
                .get("id");

        // When
        ExtractableResponse<Response> response = deleteStation(stationId);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        List<String> stationNames = getStations();
        assertThat(stationNames).doesNotContain(stationName);
    }

    ExtractableResponse<Response> createStation(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured.given().log().all()
                .basePath(basePath)
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post()
                .then().log().all()
                .extract();
    }

    List<String> getStations() {
        return RestAssured.given().log().all()
                .basePath(basePath)
                .when().get()
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    ExtractableResponse<Response> deleteStation(int stationId) {
        return RestAssured.given().log().all()
                .basePath(basePath)
                .pathParam("stationId", stationId)
                .when().delete("/{stationId}")
                .then().log().all()
                .extract();
    }
}