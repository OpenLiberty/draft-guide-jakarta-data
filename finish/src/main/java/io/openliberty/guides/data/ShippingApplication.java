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

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * The main application class for the shipping application. This class is a
 * JAX-RS application configuration class, annotated with
 * {@code @ApplicationPath} to specify the base path for all resources.
 */
@ApplicationPath("/shipping")
public class ShippingApplication extends Application {

}
