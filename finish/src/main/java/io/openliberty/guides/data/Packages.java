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

import java.util.List;

import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Find;
import jakarta.data.repository.Insert;
import jakarta.data.repository.OrderBy;
import jakarta.data.repository.Query;
import jakarta.data.Limit;
import jakarta.data.Sort;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.By;
import jakarta.data.repository.Repository;

@Repository
// tag::CrudRepository[]
public interface Packages extends CrudRepository<Package, Integer> {
    // end::CrudRepository[]

    // tag::query-by-method[]
    List<Package> findByLengthGreaterThan(float length);

    List<Package> findByLengthGreaterThanAndWidthLessThan(float length, float width);

    List<Package> findByHeightBetween(float minHeight, float maxHeight);
    // end::query-by-method[]

    // tag::annotations[]
    @Find
    List<Package> getPackagesArrivingIn(@By("destination") String destination);

    @Insert
    void add(Package p);

    @Find
    @OrderBy("height")
    List<Package> sortedByHeightAscending();
    // end::annotations[]

    // tag::sorting[]
    @Find
    List<Package> sorted(Sort<?> sortBy);

    @Find
    @OrderBy("length")
    List<Package> longestWithLimit(Limit limit);

    @Find
    Page<Package> all(PageRequest pageRequest);
    // end::sorting[]

    // tag::query-anno[]
    @Query("WHERE length > :threshold OR height > :threshold OR width > :threshold")
    List<Package> withDimensionLargerThan(float threshold);

    @Query("WHERE length + width + height > ?1")
    List<Package> withTotalDimensionOver(float threshold);
    // end::query-anno[]
}
