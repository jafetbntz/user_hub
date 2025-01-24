package dev.bntz.rest.json;

import java.util.List;
import java.util.UUID;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;

import dev.bntz.models.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/users")
@ApplicationScoped
public class UserResource {
    @Produce("direct:report")
    private ProducerTemplate reportProducer;

    @Inject
    private ProducerTemplate producer;

    public UserResource() {

    }


    /**
     * This endpoint will prompt [in the borwser] a csv file containing the 
     * list of users present in the database.
     * @return a csv file called user.csv
     */
    @GET
    @Path("report")
    public Response getReport() {

        var result = this.reportProducer
        .requestBody("direct:start");

        return Response
            .ok(result)
            .encoding("utf-8")
            .header("Content-Type", "text/csv")
            .header("Content-Disposition", "attachment; filename=users.csv")
            .build();
    }


    @GET
    public Response getAll() {

        var result = this.producer
        .requestBody("direct:getAll", null, List.class);

        return Response.ok(result).build();
    }


    @GET
    @Path("{id}")
    public Response get(UUID id) {

        var result = this.producer
        .requestBody("direct:getById", id, User.class);

        return Response.ok(result).build();
    }

    @POST
    public Response create(User user) {

        user.setId(UUID.randomUUID());

        var result = this.producer.requestBody("direct:create", user);

        return Response.ok(result).build();
    }

    @PUT
    @Path("{id}")
    public Response update(UUID id, User user) {

        user.setId(id);

        var body = this.producer
        .requestBody("direct:update", user);


        return Response.ok(body).build();
    }

    @DELETE
    @Path("{id}")
    public Response delete(UUID id) {
        var result = this.producer
            .requestBody("direct:delete", id, Boolean.class);

        var body =  new BooleanDataContainer<Object>(result);
        return Response.ok(body).build();
    }

}
