package com.roomreservation.calendarservice.documentation;

import com.roomreservation.calendarservice.dto.CalendarEntryDto;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        Components components = new Components();

        addSchemas(components);

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
                        .title("Calendar API")
                        .version("1.0")
                        .description("API documentation for Calendar operations"))
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .paths(new Paths()
                        .addPathItem("/api/calendar/day", dayReservationsPathItem())
                );
    }

    private void addSchemas(Components components) {
        var schemas = ModelConverters.getInstance().read(CalendarEntryDto.class);
        schemas.forEach(components::addSchemas);
    }

    private PathItem dayReservationsPathItem() {
        return new PathItem().get(
                new Operation()
                        .operationId("getDayReservations")
                        .summary("Get reservations for a specific day")
                        .description("Returns calendar entries for the provided date (ISO-8601 format: yyyy-MM-dd).")
                        .addParametersItem(new Parameter()
                                .name("date")
                                .in("query")
                                .required(true)
                                .schema(new StringSchema().format("date"))
                                .example("2026-02-10")
                        )
                        .responses(new ApiResponses()
                                .addApiResponse("200",
                                        new ApiResponse()
                                                .description("List of calendar entries for the day")
                                                .content(new Content().addMediaType(
                                                        "application/json",
                                                        new MediaType().schema(
                                                                new ArraySchema()
                                                                        .items(new Schema<>().$ref("#/components/schemas/CalendarEntryDto"))
                                                        )
                                                )))
                                .addApiResponse("400", new ApiResponse().description("Invalid request"))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized"))
                                .addApiResponse("403", new ApiResponse().description("Forbidden"))
                        )
        );
    }
}
