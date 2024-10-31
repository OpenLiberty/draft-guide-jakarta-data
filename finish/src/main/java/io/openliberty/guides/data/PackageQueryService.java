package io.openliberty.guides.data;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.data.Limit;
import jakarta.data.Sort;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/packageQuery")
public class PackageQueryService {

    @Inject
    Packages packages;

    //TODO see if some of these could be included
    static List<String> excludedMethods = new ArrayList<String>();
    static {
        excludedMethods.add("insert");
        excludedMethods.add("insertAll");
        excludedMethods.add("update");
        excludedMethods.add("updateAll");
        excludedMethods.add("save");
        excludedMethods.add("saveAll");
        excludedMethods.add("delete");
        excludedMethods.add("deleteAll");
        excludedMethods.add("deleteById");
    }

    static Map<String,Class<?>> primitiveMap = new HashMap<String,Class<?>>();
    static {
       primitiveMap.put("int", Integer.TYPE );
       primitiveMap.put("long", Long.TYPE );
       primitiveMap.put("double", Double.TYPE );
       primitiveMap.put("float", Float.TYPE );
       primitiveMap.put("bool", Boolean.TYPE );
       primitiveMap.put("char", Character.TYPE );
       primitiveMap.put("byte", Byte.TYPE );
       primitiveMap.put("void", Void.TYPE );
       primitiveMap.put("short", Short.TYPE );
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String queries() {
        Method[] methods = Packages.class.getMethods();
        JsonArrayBuilder queryList = Json.createArrayBuilder();

        for (Method m : methods) {
            if (excludedMethods.contains(m.getName())) continue;
            
            System.out.println();
            System.out.println("method:   " + m.getName() + "    --------");
            
            
            JsonObjectBuilder function = Json.createObjectBuilder();
            function.add("name", m.getName());
            JsonArrayBuilder params = Json.createArrayBuilder();
            JsonArrayBuilder types = Json.createArrayBuilder();
            
            System.out.println("params:   --------");
            for (Parameter p : m.getParameters()) {
                params.add(p.getName());
                types.add(p.getType().getName());
                System.out.println(p.getName());
                System.out.println(p.getType().getName());
            }
            function.add("parameters", params);
            function.add("types", types);

            queryList.add(function);
        }

        return queryList.build().toString();

    }

    @SuppressWarnings("unchecked")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String callQuery(String jsonString) {
        System.out.println(jsonString);
        JsonObject json = Json.createReader(new StringReader(jsonString)).readObject();
        try {
            List<Object> params = new ArrayList<Object>();
            List<Class<?>> types = new ArrayList<Class<?>>();
            JsonArray jsonParams = json.getJsonArray("parameters");
            JsonArray jsonTypes = json.getJsonArray("types");
            for (int i = 0; i < jsonParams.size(); i++) {
                String type = jsonTypes.getString(i);
                try {
                    if (primitiveMap.containsKey(type)) {
                        types.add(primitiveMap.get(type));
                    } else {
                        types.add(Class.forName(type));
                    }
                } catch (ClassNotFoundException e) {
                    // TODO Reply to Client with error message
                    e.printStackTrace();
                }
                params.add(getTypedValue(jsonParams,i,types.get(i)));
            }


            Method method = Packages.class.getMethod(json.getString("method"), types.toArray(new Class<?>[0]));
            checkForID(method, params);
            Object result = method.invoke(packages, params.toArray());

            JsonArrayBuilder returnList = Json.createArrayBuilder();
            if (result instanceof Stream) {
                ((Stream<?>)result).forEach(p -> {
                    returnList.add(((Package) p).toJson());
                });
            } else if (result instanceof List) {
                ((List<?>)result).forEach(p -> {
                    returnList.add(((Package) p).toJson());
                });
            } else if (result instanceof Optional) {
                returnList.add(((Optional<Package>) result).get().toJson());
            } else {
                throw new UnsupportedOperationException("Return type " + result.getClass() + " not handled in PackageQueryService.java");
            }
            return returnList.build().toString();
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // TODO Reply to Client with error message
            e.printStackTrace();
        }
        return "";
    }

    Object getTypedValue(JsonArray array, int index, Class<?> type) {
        System.out.println("type: " + type);
        //Numbers
        if (type.equals(Integer.class) || type.equals(Integer.TYPE)) return Integer.parseInt(array.getString(index));
        else if (type.equals(Long.class) || type.equals(Long.TYPE)) return Long.parseLong(array.getString(index));
        else if (type.equals(Float.class) || type.equals(Float.TYPE)) return Float.parseFloat(array.getString(index));
        else if (type.equals(Double.class) || type.equals(Double.TYPE)) return array.getJsonNumber(index).doubleValue();
        //Sorts
        else if (type.equals(Sort.class)) return parseSort(array.getString(index));
        //Limit
        else if (type.equals(Limit.class)) return Limit.of(Integer.parseInt(array.getString(index)));
        //Strings
        else return array.getString(index);
    }

    Sort<?> parseSort(String sort) {
        System.out.println("sort: " + sort);
        if (sort.endsWith("asc")) {
            return Sort.asc(sort.substring(0, sort.lastIndexOf(" asc")));
        } else {
            return Sort.desc(sort.substring(0, sort.lastIndexOf(" desc")));
        }

    }

    //Due to type erasure we need to handle id as a special case
    void checkForID(Method method, List<Object> params) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals("id")) {
                params.set(i, Integer.parseInt((String)params.get(i)));
            }
        }
    }

    boolean excludedMethods(Method m) {
        return excludedMethods.contains(m.getName());
    }
}
