// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2024, 2025 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.data;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.data.Limit;
import jakarta.data.Sort;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
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
import jakarta.ws.rs.core.Response;

/**
 * A RESTful Web Services resource that provides functionality for querying
 * packages. It provides a list of available queries in the {@link Packages}
 * class as well as allows those methods to be called through reflection.
 */
@Path("/packageQuery")
public class PackageQueryService {

    @Inject
    Packages packages;

    /**
     * Methods that aren't queries or won't make sense in the UI
     */
    static List<String> excludedMethods = new ArrayList<String>();
    static {
        excludedMethods.add("add");
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

    static Map<String, Class<?>> primitiveMap = new HashMap<String, Class<?>>();
    static {
        primitiveMap.put("int", Integer.TYPE);
        primitiveMap.put("long", Long.TYPE);
        primitiveMap.put("double", Double.TYPE);
        primitiveMap.put("float", Float.TYPE);
        primitiveMap.put("bool", Boolean.TYPE);
        primitiveMap.put("char", Character.TYPE);
        primitiveMap.put("byte", Byte.TYPE);
        primitiveMap.put("void", Void.TYPE);
        primitiveMap.put("short", Short.TYPE);
    }

    /**
     * GET method to return an array of the available query methods from the
     * {@link Packages} class.
     *
     * @return A JSON array containing details of available query methods.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String queries() {
        Method[] methods = Packages.class.getMethods();
        JsonArrayBuilder queryList = Json.createArrayBuilder();

        for (Method m : methods) {
            if (excludeMethod(m)) {
                continue;
            }

            JsonObjectBuilder function = Json.createObjectBuilder();
            function.add("name", m.getName());
            JsonArrayBuilder params = Json.createArrayBuilder();
            JsonArrayBuilder types = Json.createArrayBuilder();

            for (Parameter p : m.getParameters()) {
                params.add(p.getName());
                types.add(p.getType().getName());
            }
            function.add("parameters", params);
            function.add("types", types);

            queryList.add(function);
        }

        return queryList.build().toString();

    }

    /**
     * POST method to execute a query method based on the provided JSON input.
     *
     * @param jsonString JSON string containing method name, parameters, and their
     *                   types.
     * @return A JSON array of results from the executed method.
     */
    @SuppressWarnings("unchecked")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
    public Response callQuery(String jsonString) {
        JsonObject json = Json.createReader(new StringReader(jsonString)).readObject();
        JsonArrayBuilder returnList = Json.createArrayBuilder();
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
                    e.printStackTrace();
                    return Response.status(404).type(MediaType.TEXT_PLAIN)
                            .entity("The requested Query does not exist.").build();
                }
                params.add(getTypedValue(jsonParams, i, types.get(i)));
            }

            Method method = Packages.class.getMethod(json.getString("method"),
                    types.toArray(new Class<?>[0]));
            checkForID(method, params);
            Object result = method.invoke(packages, params.toArray());

            if (result instanceof Stream) {
                ((Stream<?>) result).forEach(p -> {
                    returnList.add(toJson((Package) p));
                });
            } else if (result instanceof List) {
                ((List<?>) result).forEach(p -> {
                    returnList.add(toJson((Package) p));
                });
            } else if (result instanceof Page) {
                ((Page<?>) result).forEach(p -> {
                    returnList.add(toJson((Package) p));
                });
            } else if (result instanceof Optional) {
                returnList.add(toJson(((Optional<Package>) result).get()));
            } else {
                throw new UnsupportedOperationException(
                        "Return type " + result.getClass()
                                + " not handled in PackageQueryService.java");
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            if (e instanceof InvocationTargetException) {
                if (e.getCause() != null) {
                    return Response.status(404).type(MediaType.TEXT_PLAIN)
                            .entity(e.getCause().toString()).build();
                }
            } else if (e instanceof IllegalArgumentException) {
                return Response.status(404).type(MediaType.TEXT_PLAIN)
                        .entity("Invalid input for selected method. " + e.getMessage())
                        .build();
            } else {
                e.printStackTrace();
                return Response.status(404).type(MediaType.TEXT_PLAIN)
                        .entity("Error. " + e.getMessage()).build();
            }
        }
        return Response.ok(returnList.build().toString(), MediaType.APPLICATION_JSON)
                .build();
    }

    Object getTypedValue(JsonArray array, int index, Class<?> type) {
        // Numbers
        if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return Integer.parseInt(array.getString(index));
        } else if (type.equals(Long.class) || type.equals(Long.TYPE)) {
            return Long.parseLong(array.getString(index));
        } else if (type.equals(Float.class) || type.equals(Float.TYPE)) {
            return Float.parseFloat(array.getString(index));
        } else if (type.equals(Double.class) || type.equals(Double.TYPE)) {
            return array.getJsonNumber(index).doubleValue();
            // Sorts
        } else if (type.equals(Sort.class)) {
            return parseSort(array.getString(index));
            // Limit
        } else if (type.equals(Limit.class)) {
            return parseLimit(array.getString(index));
            // PageRequest
        } else if (type.equals(PageRequest.class)) {
            return parsePageRequest(array.getString(index));
            // Strings
        } else {
            return array.getString(index);
        }
    }

    Sort<?> parseSort(String sort) {
        if (sort.endsWith("asc")) {
            return Sort.asc(sort.substring(0, sort.lastIndexOf(" asc")));
        } else {
            return Sort.desc(sort.substring(0, sort.lastIndexOf(" desc")));
        }

    }

    Limit parseLimit(String limit) {
        if (limit.contains("-")) {
            String[] split = limit.split("-");
            return Limit.range(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        } else {
            return Limit.of(Integer.parseInt(limit));
        }
    }

    PageRequest parsePageRequest(String pageRequest) {
        if (pageRequest.contains(",")) {
            String[] split = pageRequest.split(",");
            return PageRequest.ofPage(Long.parseLong(split[0]),
                    Integer.parseInt(split[1]), false);
        }
        return PageRequest.ofPage(Long.parseLong(pageRequest));
    }

    // Due to type erasure we need to handle id as a special case
    void checkForID(Method method, List<Object> params) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals("id")) {
                params.set(i, Integer.parseInt((String) params.get(i)));
            }
        }
    }

    boolean excludeMethod(Method m) {
        if (excludedMethods.contains(m.getName())) {
            return true;
        }
        // exclude methods that accept an Order
        if (Arrays.asList(m.getParameterTypes()).contains(jakarta.data.Order.class)) {
            return true;
        }
        return false;
    }

    /**
     * Helper method to convert packages to Json
     */
    JsonObjectBuilder toJson(Package p) {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("id", p.id());
        json.add("length", p.length());
        json.add("width", p.width());
        json.add("height", p.height());
        json.add("destination", p.destination());
        return json;
    }

}
