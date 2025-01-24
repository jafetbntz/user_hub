package dev.bntz.services;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import io.quarkus.runtime.annotations.RegisterForReflection;
import dev.bntz.models.User;
import dev.bntz.rest.json.BooleanDataContainer;

@ApplicationScoped
@Named("userService")
@RegisterForReflection
public class UserService {

    private List<User> users = new ArrayList<User>();

    public UserService() {
        var user1 =  new User();
        user1.setFullName("Luke Skywalker");
        user1.setWhatsApp("123454654654");
        user1.setEmail("foo@test.com");
        user1.setId(UUID.randomUUID());
        
        users.add(user1);

        var user2 =  new User();
        user2.setFullName("Obiwan Kenobi");
        user2.setWhatsApp("8295989363");
        user2.setEmail("obi@test.com");
        user2.setId(UUID.randomUUID());
        
        users.add(user2);
    }

    public List<User> getAll() {
        return this.users;
    }

    /**
     * This method fetches the information from the list and
     * returns a List<Maps, Object>.
     */
    public List<Map<String, Object>> getReport()  {
        return this.users
            .stream()
            .map(u -> this.convertToMap(u))
            .collect(Collectors.toList());
    }

    public User get(UUID id) {
        
        var result = this.users
            .stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .orElse(null);

        return result;
    }

    public BooleanDataContainer<User> update(User user) {
    
        var wasModified =  false;
        for (User u : users) {
            if (!u.getId().equals(user.getId())) continue;

            u.setEmail(user.getEmail());
            u.setWhatsApp(user.getWhatsApp());
            u.setFullName(user.getFullName());
            u.setRole(user.getRole());

            wasModified =  true;
            
        }

        return new BooleanDataContainer<User>(wasModified, user);
    }

    public BooleanDataContainer<User> create(User user) {
        user.setId(UUID.randomUUID());
        users.add(user);
        return new BooleanDataContainer<User>(true, user);
    }

    public Boolean delete(UUID id) {
        var result = users.removeIf(u -> u.getId().equals(id));
        return result;
    }


    // ----------------------------------------------------
    // Helpers
    // ----------------------------------------------------

    /**
     * This method converts any Object into a Map where the keys 
     * are the names of the class properties and the value is the
     * one provided by the object
     * 
     *  @param object expects the object to ve converted
     *  @return a Map containing the object's information
     **/
    private Map<String, Object> convertToMap(Object object) {
        try {
            Map<String, Object> map = new HashMap<>();
            Field[] fields = object.getClass().getDeclaredFields();
            
            for (Field field: fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(object));
            }
        
            return map;
        } catch(Exception e) {
            return null;
        }
    }
}
