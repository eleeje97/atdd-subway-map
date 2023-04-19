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

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLineTest() {
        // When
        Map<String, Object> params = new HashMap<>();
        params.put("name", SILLIM_LINE);
        params.put("color", "bg-navy-600");
        params.put("upStationId", 1);
        params.put("downStationId", 2);
        params.put("distance", 10);

        RestAssured.given().log().all()
                .basePath("lines")
                .body(params)
                .when().post()
                .then().log().all()
                .assertThat().statusCode(HttpStatus.CREATED.value())
                .extract();

        // Then
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .basePath("lines")
                .when().get()
                .then().log().all()
                .assertThat().statusCode(HttpStatus.CREATED.value())
                .extract();

        List<String> lineNames = response.jsonPath().getList("name", String.class);
        assertThat(lineNames).contains(SILLIM_LINE);
    }

}
