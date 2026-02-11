package com.roomreservation.authenticationservice.documentation;

import com.roomreservation.authenticationservice.dto.AuthResponse;
import com.roomreservation.authenticationservice.dto.CompleteRegistrationRequest;
import com.roomreservation.authenticationservice.dto.DisableUserRequest;
import com.roomreservation.authenticationservice.dto.InviteUserRequest;
import com.roomreservation.authenticationservice.dto.LoginRequest;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        Components components = new Components();

        addSchemas(components, InviteUserRequest.class);
        addSchemas(components, CompleteRegistrationRequest.class);
        addSchemas(components, LoginRequest.class);
        addSchemas(components, AuthResponse.class);
        addSchemas(components, DisableUserRequest.class);

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
                        .title("Authentication API")
                        .version("1.0")
                        .description("API documentation for Authentication operations"))
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .paths(new Paths()
                        .addPathItem("/api/auth/invite", invitePathItem())
                        .addPathItem("/api/auth/complete-registration", completeRegistrationPathItem()) // public
                        .addPathItem("/api/auth/login", loginPathItem())
                        .addPathItem("/api/auth/users/disable", disableUserPathItem())
                );
    }

    private void addSchemas(Components components, Class<?> cls) {
        var schemas = ModelConverters.getInstance().read(cls);
        schemas.forEach(components::addSchemas);
    }

    private PathItem invitePathItem() {
        return new PathItem().post(
                new Operation()
                        .operationId("inviteUser")
                        .summary("Invite user")
                        .requestBody(new RequestBody()
                                .required(true)
                                .content(new Content().addMediaType(
                                        "application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/InviteUserRequest"))
                                )))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse().description("Invitation sent"))
                                .addApiResponse("400", new ApiResponse().description("Invalid request"))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized"))
                                .addApiResponse("403", new ApiResponse().description("Forbidden"))
                        )
        );
    }

    private PathItem completeRegistrationPathItem() {
        return new PathItem().post(
                new Operation()
                        .operationId("completeRegistration")
                        .summary("Complete registration")
                        .description("Public endpoint: completes registration without Bearer token.")
                        .security(List.of())
                        .requestBody(new RequestBody()
                                .required(true)
                                .content(new Content().addMediaType(
                                        "application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/CompleteRegistrationRequest"))
                                )))
                        .responses(new ApiResponses()
                                .addApiResponse("200", new ApiResponse().description("Registration completed"))
                                .addApiResponse("400", new ApiResponse().description("Invalid request"))
                        )
        );
    }

    private PathItem loginPathItem() {
        return new PathItem().post(
                new Operation()
                        .operationId("login")
                        .summary("Login")
                        .requestBody(new RequestBody()
                                .required(true)
                                .content(new Content().addMediaType(
                                        "application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/LoginRequest"))
                                )))
                        .responses(new ApiResponses()
                                .addApiResponse("200",
                                        new ApiResponse()
                                                .description("Authenticated")
                                                .content(new Content().addMediaType(
                                                        "application/json",
                                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/AuthResponse"))
                                                )))
                                .addApiResponse("400", new ApiResponse().description("Invalid request"))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized"))
                                .addApiResponse("403", new ApiResponse().description("Forbidden"))
                        )
        );
    }

    private PathItem disableUserPathItem() {
        return new PathItem().patch(
                new Operation()
                        .operationId("disableUser")
                        .summary("Disable user")
                        .requestBody(new RequestBody()
                                .required(true)
                                .content(new Content().addMediaType(
                                        "application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/DisableUserRequest"))
                                )))
                        .responses(new ApiResponses()
                                .addApiResponse("204", new ApiResponse().description("User disabled"))
                                .addApiResponse("400", new ApiResponse().description("Invalid request"))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized"))
                                .addApiResponse("403", new ApiResponse().description("Forbidden"))
                        )
        );
    }
}
