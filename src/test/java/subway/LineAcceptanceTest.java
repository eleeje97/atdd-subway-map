package subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql("/truncate.sql")
public class LineAcceptanceTest {
    private static final String basePath = "/lines";

    private static final String SILLIM_LINE = "신림선";
    private static final String EVER_LINE = "에버라인";

    private static final String NAVY_COLOR = "bg-navy-600";
    private static final String YELLOW_COLOR = "bg-yellow-600";

    private static final String SAETGANG_STATION = "샛강역";
    private static final String GWANAKSAN_STATION = "관악산역";
    private static final String GIHEUNG_STATION = "기흥역";
    private static final String EVERLAND_STATION = "에버랜드역";

    @BeforeEach
    void registerStations() {
        지하철역_생성(SAETGANG_STATION);
        지하철역_생성(GWANAKSAN_STATION);
        지하철역_생성(GIHEUNG_STATION);
        지하철역_생성(EVERLAND_STATION);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLineTest() {
        // When
        지하철노선_생성(SILLIM_LINE, NAVY_COLOR, 1, 2, 10);

        // Then
        List<String> lineNames = 지하철노선_목록_조회();
        assertThat(lineNames).contains(SILLIM_LINE);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLineListTest() {
        // Given
        지하철노선_생성(SILLIM_LINE, NAVY_COLOR, 1, 2, 10);
        지하철노선_생성(EVER_LINE, YELLOW_COLOR, 1, 3, 15);

        // When
        List<String> lineNames = 지하철노선_목록_조회();

        // Then
        assertThat(lineNames).hasSize(2);
        assertThat(lineNames).contains(SILLIM_LINE, EVER_LINE);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLineTest() {
        // Given
        지하철노선_생성(SILLIM_LINE, NAVY_COLOR, 1, 2, 10);

        // When
        ExtractableResponse<Response> response = 지하철노선_조회(1);

        // Then
        assertThat(response.jsonPath().getInt("id")).isEqualTo(1);
        assertThat(response.jsonPath().getString("name")).isEqualTo(SILLIM_LINE);
        assertThat(response.jsonPath().getString("color")).isEqualTo(NAVY_COLOR);
        assertThat(response.jsonPath().getList("stations")).hasSize(2);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLineTest() {
        // Given
        지하철노선_생성(SILLIM_LINE, NAVY_COLOR, 1, 2, 10);

        // When
        지하철노선_수정(1,  EVER_LINE, NAVY_COLOR);

        // Then
        ExtractableResponse<Response> response = 지하철노선_조회(1);
        assertThat(response.jsonPath().getString("name")).isEqualTo(EVER_LINE);
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLineTest() {
        // Given
        지하철노선_생성(SILLIM_LINE, NAVY_COLOR, 1, 2, 10);

        // When
        지하철노선_삭제(1);

        // Then
        List<String> lineNames = 지하철노선_목록_조회();
        assertThat(lineNames).doesNotContain(SILLIM_LINE);
    }


    private void 지하철노선_생성(String name, String color, int upStationId, int downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        RestAssured.given().log().all()
                .basePath(basePath)
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post()
                .then().log().all()
                .assertThat().statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    private List<String> 지하철노선_목록_조회() {
        return RestAssured.given().log().all()
                .basePath(basePath)
                .when().get()
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList("name", String.class);
    }

    private ExtractableResponse<Response> 지하철노선_조회(int lineId) {
        return RestAssured.given().log().all()
                .basePath(basePath)
                .pathParam("lineId", lineId)
                .when().get("/{lineId}")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value())
                .extract();
    }

    private void 지하철노선_수정(int lineId, String name, String color) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        RestAssured.given().log().all()
                .basePath(basePath)
                .body(params)
                .pathParam("lineId", lineId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/{lineId}")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value())
                .extract();
    }

    private void 지하철노선_삭제(int lineId) {
        RestAssured.given().log().all()
                .basePath(basePath)
                .pathParam("lineId", lineId)
                .when().delete("/{lineId}")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

    private void 지하철역_생성(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);

        RestAssured.given()
                .basePath("stations")
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post()
                .then()
                .extract();
    }
}
