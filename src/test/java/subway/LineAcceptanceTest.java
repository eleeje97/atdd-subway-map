package subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LineAcceptanceTest {
    private static final String SILLIM_LINE = "신림선";
    private static final String EVER_LINE = "에버라인";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLineTest() {
        // When
        지하철노선_생성(SILLIM_LINE, "bg-navy-600", 1, 2, 10);

        // Then
        List<String> lineNames = 지하철노선_목록_조회();
        assertThat(lineNames).contains(SILLIM_LINE);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLineListTest() {
        // Given
        지하철노선_생성(SILLIM_LINE, "bg-navy-600", 1, 2, 10);
        지하철노선_생성(EVER_LINE, "bg-yellow-600", 1, 3, 15);

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
        지하철노선_생성(SILLIM_LINE, "bg-navy-600", 1, 2, 10);

        // When
        ExtractableResponse<Response> response = 지하철노선_조회(1);

        // Then
        assertThat(response.jsonPath().getInt("id")).isEqualTo(1);
        assertThat(response.jsonPath().getString("name")).isEqualTo(SILLIM_LINE);
        assertThat(response.jsonPath().getString("color")).isEqualTo("bg-navy-600");
        assertThat(response.jsonPath().getList("stations")).hasSize(2);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLineTest() {
        // Given
        지하철노선_생성(SILLIM_LINE, "bg-navy-600", 1, 2, 10);

        // When
        지하철노선_수정(1,  EVER_LINE, "bg-navy-600");

        // Then
        ExtractableResponse<Response> response = 지하철노선_조회(1);
        assertThat(response.jsonPath().getString("name")).isEqualTo(EVER_LINE);
    }


    private void 지하철노선_생성(String name, String color, int upStationId, int downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        RestAssured.given().log().all()
                .basePath("lines")
                .body(params)
                .when().post()
                .then().log().all()
                .assertThat().statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    private List<String> 지하철노선_목록_조회() {
        return RestAssured.given().log().all()
                .basePath("lines")
                .when().get()
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value())
                .extract().jsonPath().getList("name", String.class);
    }

    private ExtractableResponse<Response> 지하철노선_조회(int lineId) {
        return RestAssured.given().log().all()
                .basePath("lines")
                .pathParam("lineId", lineId)
                .when().get("/{lineId}")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value())
                .extract();
    }

    private void 지하철노선_수정(int lindId, String name, String color) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        RestAssured.given().log().all()
                .basePath("lines")
                .body(params)
                .pathParam("lineId", lindId)
                .when().put("/{lindId}")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value())
                .extract();
    }
}
