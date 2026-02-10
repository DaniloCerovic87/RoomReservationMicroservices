package com.roomreservation.reservationservice.documentation;

import com.roomreservation.reservationservice.dto.ReservationRequest;
import com.roomreservation.reservationservice.dto.ReservationResponse;
import com.roomreservation.reservationservice.dto.ReviewReservationRequest;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenAPIConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        Components components = new Components();

        addSchemas(components, ReservationRequest.class);
        addSchemas(components, ReservationResponse.class);
        addSchemas(components, ReviewReservationRequest.class);

        components.addSecuritySchemes(
                SECURITY_SCHEME_NAME,
                new SecurityScheme()
                        .name("Authorization")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
        );

        return new OpenAPI()
                .info(new Info()
                        .title("Reservation API")
                        .version("1.0")
                        .description("API documentation for Reservation operations"))
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .paths(new Paths()
                        .addPathItem("/api/reservations", createReservationPathItem())
                        .addPathItem("/api/reservations/{reservationId}/review", reviewReservationPathItem())
                        .addPathItem("/api/reservations/busy-room-ids", busyRoomIdsPathItem())
                );
    }

    private void addSchemas(Components components, Class<?> cls) {
        var schemas = ModelConverters.getInstance().read(cls);
        schemas.forEach(components::addSchemas);
    }

    private PathItem createReservationPathItem() {
        return new PathItem().post(
                new Operation()
                        .operationId("createReservation")
                        .summary("Create a new reservation")
                        .requestBody(new RequestBody()
                                .required(true)
                                .content(new Content().addMediaType(
                                        "application/json",
                                        new MediaType().schema(
                                                new Schema<>().$ref("#/components/schemas/ReservationRequest")
                                        )
                                )))
                        .responses(new ApiResponses()
                                .addApiResponse("201",
                                        new ApiResponse()
                                                .description("Reservation created")
                                                .content(new Content().addMediaType(
                                                        "application/json",
                                                        new MediaType().schema(
                                                                new Schema<>().$ref("#/components/schemas/ReservationResponse")
                                                        )
                                                )))
                                .addApiResponse("400", new ApiResponse().description("Invalid request"))
                        )
        );
    }

    private PathItem reviewReservationPathItem() {
        return new PathItem().post(
                new Operation()
                        .operationId("reviewReservation")
                        .summary("Review reservation rooms")
                        .addParametersItem(new Parameter()
                                .name("reservationId")
                                .in(ParameterIn.PATH.toString())
                                .required(true)
                                .schema(new IntegerSchema().format("int64")))
                        .requestBody(new RequestBody()
                                .required(true)
                                .content(new Content().addMediaType(
                                        "application/json",
                                        new MediaType().schema(
                                                new Schema<>().$ref("#/components/schemas/ReviewReservationRequest")
                                        )
                                )))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse().description("Reservation reviewed"))
                                .addApiResponse("400", new ApiResponse().description("Invalid request"))
                                .addApiResponse("404", new ApiResponse().description("Reservation not found"))
                        )
        );
    }

    private PathItem busyRoomIdsPathItem() {
        return new PathItem().get(
                new Operation()
                        .operationId("busyRoomIds")
                        .summary("Get busy room IDs")
                        .parameters(Arrays.asList(
                                new Parameter()
                                        .name("startTime")
                                        .in("query")
                                        .required(true)
                                        .schema(new StringSchema().format("date-time")),
                                new Parameter()
                                        .name("endTime")
                                        .in("query")
                                        .required(true)
                                        .schema(new StringSchema().format("date-time"))
                        ))
                        .responses(new ApiResponses()
                                .addApiResponse("200",
                                        new ApiResponse()
                                                .description("List of busy room IDs")
                                                .content(new Content().addMediaType(
                                                        "application/json",
                                                        new MediaType().schema(
                                                                new ArraySchema().items(new IntegerSchema().format("int64"))
                                                        )
                                                )))
                                .addApiResponse("400", new ApiResponse().description("Invalid request"))
                        )
        );
    }
}
