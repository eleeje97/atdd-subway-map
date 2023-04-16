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

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStationTest() {
        // Given
        String GANGNAM_STATION = "강남역";

        // When
        지하철역_생성(GANGNAM_STATION);

        // Then
        List<String> stationNames = 지하철역_조회();
        assertThat(stationNames).contains(GANGNAM_STATION);
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void getStationsTest() {
        // Given
        지하철역_생성("강남역");
        지하철역_생성("서울역");

        // When
        List<String> stationNames = 지하철역_조회();

        // Then
        assertThat(stationNames.size()).isEqualTo(2);
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStationTest() {
        // Given
        String GANGNAM_STATION = "강남역";
        int stationId = 지하철역_생성(GANGNAM_STATION)
                .body()
                .jsonPath()
                .get("id");

        // When
        지하철역_삭제(stationId);

        // Then
        List<String> stationNames = 지하철역_조회();
        assertThat(stationNames).doesNotContain(GANGNAM_STATION);
    }

    private ExtractableResponse<Response> 지하철역_생성(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        return RestAssured.given().log().all()
                .basePath(basePath)
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post()
                .then().log().all()
                .assertThat().statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    private List<String> 지하철역_조회() {
        return RestAssured.given().log().all()
                .basePath(basePath)
                .when().get()
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    private ExtractableResponse<Response> 지하철역_삭제(int stationId) {
        return RestAssured.given().log().all()
                .basePath(basePath)
                .pathParam("stationId", stationId)
                .when().delete("/{stationId}")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }
}