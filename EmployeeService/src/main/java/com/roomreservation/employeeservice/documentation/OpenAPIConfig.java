package com.roomreservation.employeeservice.documentation;

import com.roomreservation.employeeservice.dto.CreateEmployeeRequest;
import com.roomreservation.employeeservice.dto.EmployeeResponse;
import com.roomreservation.employeeservice.dto.UpdateEmployeeRequest;
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

        addSchemas(components, CreateEmployeeRequest.class);
        addSchemas(components, UpdateEmployeeRequest.class);
        addSchemas(components, EmployeeResponse.class);

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
                        .title("Employee API")
                        .version("1.0")
                        .description("API documentation for Employee operations"))
                .components(components)
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .paths(new Paths()
                        .addPathItem("/api/employees", employeesPathItem())
                        .addPathItem("/api/employees/{id}", employeeByIdPathItem())
                );
    }

    private void addSchemas(Components components, Class<?> cls) {
        var schemas = ModelConverters.getInstance().read(cls);
        schemas.forEach(components::addSchemas);
    }

    private PathItem employeesPathItem() {
        return new PathItem()
                .get(new Operation()
                        .operationId("getAllEmployees")
                        .summary("Get all employees")
                        .responses(new ApiResponses()
                                .addApiResponse("200",
                                        new ApiResponse()
                                                .description("List of employees")
                                                .content(new Content().addMediaType(
                                                        "application/json",
                                                        new MediaType().schema(
                                                                new ArraySchema()
                                                                        .items(new Schema<>().$ref("#/components/schemas/EmployeeResponse"))
                                                        )
                                                )))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized"))
                                .addApiResponse("403", new ApiResponse().description("Forbidden"))
                        )
                )
                .post(new Operation()
                        .operationId("createEmployee")
                        .summary("Create employee")
                        .requestBody(new RequestBody()
                                .required(true)
                                .content(new Content().addMediaType(
                                        "application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/CreateEmployeeRequest"))
                                )))
                        .responses(new ApiResponses()
                                .addApiResponse("200",
                                        new ApiResponse()
                                                .description("Employee created")
                                                .content(new Content().addMediaType(
                                                        "application/json",
                                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/EmployeeResponse"))
                                                )))
                                .addApiResponse("400", new ApiResponse().description("Invalid request"))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized"))
                                .addApiResponse("403", new ApiResponse().description("Forbidden"))
                        )
                );
    }

    private PathItem employeeByIdPathItem() {
        Parameter idParam = new Parameter()
                .name("id")
                .in(ParameterIn.PATH.toString())
                .required(true)
                .schema(new IntegerSchema().format("int64"));

        return new PathItem()
                .get(new Operation()
                        .operationId("getEmployee")
                        .summary("Get employee by ID")
                        .addParametersItem(idParam)
                        .responses(new ApiResponses()
                                .addApiResponse("200",
                                        new ApiResponse()
                                                .description("Employee")
                                                .content(new Content().addMediaType(
                                                        "application/json",
                                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/EmployeeResponse"))
                                                )))
                                .addApiResponse("404", new ApiResponse().description("Employee not found"))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized"))
                                .addApiResponse("403", new ApiResponse().description("Forbidden"))
                        )
                )
                .put(new Operation()
                        .operationId("updateEmployee")
                        .summary("Update employee")
                        .addParametersItem(idParam)
                        .requestBody(new RequestBody()
                                .required(true)
                                .content(new Content().addMediaType(
                                        "application/json",
                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/UpdateEmployeeRequest"))
                                )))
                        .responses(new ApiResponses()
                                .addApiResponse("200",
                                        new ApiResponse()
                                                .description("Employee updated")
                                                .content(new Content().addMediaType(
                                                        "application/json",
                                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/EmployeeResponse"))
                                                )))
                                .addApiResponse("400", new ApiResponse().description("Invalid request"))
                                .addApiResponse("404", new ApiResponse().description("Employee not found"))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized"))
                                .addApiResponse("403", new ApiResponse().description("Forbidden"))
                        )
                )
                .delete(new Operation()
                        .operationId("deleteEmployee")
                        .summary("Delete employee")
                        .addParametersItem(idParam)
                        .responses(new ApiResponses()
                                .addApiResponse("204", new ApiResponse().description("Employee deleted"))
                                .addApiResponse("404", new ApiResponse().description("Employee not found"))
                                .addApiResponse("401", new ApiResponse().description("Unauthorized"))
                                .addApiResponse("403", new ApiResponse().description("Forbidden"))
                        )
                );
    }
}
