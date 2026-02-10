package com.roomreservation.roomservice.documentation;

import com.roomreservation.roomservice.dto.RoomCreateRequest;
import com.roomreservation.roomservice.dto.RoomResponse;
import com.roomreservation.roomservice.dto.RoomUpdateRequest;
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
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
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

        addSchemas(components, RoomResponse.class);
        addSchemas(components, RoomCreateRequest.class);
        addSchemas(components, RoomUpdateRequest.class);

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
                        .title("Room API")
                        .version("1.0")
                        .description("API documentation for Room operations"))
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .paths(new Paths()
                        .addPathItem("/api/rooms", roomsPathItem())
                        .addPathItem("/api/rooms/{id}", roomByIdPathItem())
                );
    }

    private void addSchemas(Components components, Class<?> cls) {
        var schemas = ModelConverters.getInstance().read(cls);
        schemas.forEach(components::addSchemas);
    }

    private PathItem roomsPathItem() {
        return new PathItem()
                .get(new Operation()
                        .operationId("getAllRooms")
                        .summary("Get all rooms")
                        .responses(new ApiResponses()
                                .addApiResponse("200",
                                        new ApiResponse()
                                                .description("List of rooms")
                                                .content(new Content().addMediaType(
                                                        "application/json",
                                                        new MediaType().schema(
                                                                new ArraySchema()
                                                                        .items(new Schema<>().$ref("#/components/schemas/RoomResponse"))
                                                        )
                                                )))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized"))
                                .addApiResponse("403", new ApiResponse().description("Forbidden"))
                        )
                )
                .post(new Operation()
                        .operationId("createRoom")
                        .summary("Create a room")
                        .requestBody(new RequestBody()
                                .required(true)
                                .content(new Content().addMediaType(
                                        "application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/RoomCreateRequest"))
                                )))
                        .responses(new ApiResponses()
                                .addApiResponse("201",
                                        new ApiResponse()
                                                .description("Room created")
                                                .content(new Content().addMediaType(
                                                        "application/json",
                                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/RoomResponse"))
                                                )))
                                .addApiResponse("400", new ApiResponse().description("Invalid request"))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized"))
                                .addApiResponse("403", new ApiResponse().description("Forbidden"))
                        )
                );
    }

    private PathItem roomByIdPathItem() {
        Parameter idParam = new Parameter()
                .name("id")
                .in(ParameterIn.PATH.toString())
                .required(true)
                .schema(new IntegerSchema().format("int64"));

        return new PathItem()
                .get(new Operation()
                        .operationId("getRoomById")
                        .summary("Get room by ID")
                        .addParametersItem(idParam)
                        .responses(new ApiResponses()
                                .addApiResponse("200",
                                        new ApiResponse()
                                                .description("Room")
                                                .content(new Content().addMediaType(
                                                        "application/json",
                                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/RoomResponse"))
                                                )))
                                .addApiResponse("404", new ApiResponse().description("Room not found"))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized"))
                                .addApiResponse("403", new ApiResponse().description("Forbidden"))
                        )
                )
                .put(new Operation()
                        .operationId("updateRoom")
                        .summary("Update room")
                        .addParametersItem(idParam)
                        .requestBody(new RequestBody()
                                .required(true)
                                .content(new Content().addMediaType(
                                        "application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/RoomUpdateRequest"))
                                )))
                        .responses(new ApiResponses()
                                .addApiResponse("200",
                                        new ApiResponse()
                                                .description("Room updated")
                                                .content(new Content().addMediaType(
                                                        "application/json",
                                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/RoomResponse"))
                                                )))
                                .addApiResponse("400", new ApiResponse().description("Invalid request"))
                                .addApiResponse("404", new ApiResponse().description("Room not found"))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized"))
                                .addApiResponse("403", new ApiResponse().description("Forbidden"))
                        )
                )
                .delete(new Operation()
                        .operationId("deleteRoom")
                        .summary("Delete room")
                        .addParametersItem(idParam)
                        .responses(new ApiResponses()
                                .addApiResponse("204", new ApiResponse().description("Room deleted"))
                                .addApiResponse("404", new ApiResponse().description("Room not found"))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized"))
                                .addApiResponse("403", new ApiResponse().description("Forbidden"))
                        )
                );
    }
}
