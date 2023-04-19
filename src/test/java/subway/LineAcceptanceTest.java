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
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .basePath("lines")
                .when().get()
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value())
                .extract();

        List<String> lineNames = response.jsonPath().getList("name", String.class);
        assertThat(lineNames).contains(SILLIM_LINE);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLineList() {
        // Given
        지하철노선_생성(SILLIM_LINE, "bg-navy-600", 1, 2, 10);
        지하철노선_생성(EVER_LINE, "bg-yellow-600", 1, 3, 15);

        // When
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .basePath("lines")
                .when().get()
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value())
                .extract();
        List<String> lineNames = response.jsonPath().getList("name", String.class);

        // Then
        assertThat(lineNames).hasSize(2);
        assertThat(lineNames).contains(SILLIM_LINE, EVER_LINE);

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
}
