// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.data;

// tag::app-only[]
import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
// end::app-only[]
public record Package(int id, float length, float width, float height, String destination) {
    // tag::app-only[]

    JsonObjectBuilder toJson() {
        JsonObjectBuilder json = Json.createObjectBuilder();
        json.add("id", id);
        json.add("length", length);
        json.add("width", width);
        json.add("height", height);
        json.add("destination", destination);
        return json;
    }
    // end::app-only[]
}
