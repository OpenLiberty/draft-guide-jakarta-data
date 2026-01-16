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

// tag::query-by-method[]
import java.util.List;
// end::query-by-method[]

import jakarta.data.repository.Repository;
import jakarta.data.repository.CrudRepository;
// tag::annotations[]
import jakarta.data.repository.Find;
import jakarta.data.repository.By;
import jakarta.data.repository.Insert;
import jakarta.data.repository.OrderBy;
// end::annotations[]
// tag::query-anno[]
import jakarta.data.repository.Query;
// end::query-anno[]
// tag::sorting[]
import jakarta.data.Limit;
import jakarta.data.Sort;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
// end::sorting[]

@Repository
// tag::CrudRepository[]
public interface Packages extends CrudRepository<Package, Integer> {
    // end::CrudRepository[]
    // tag::query-by-method[]

    // tag::findByLengthGreaterThan[]
    List<Package> findByLengthGreaterThan(float length);
    // end::findByLengthGreaterThan[]

    List<Package> findByLengthGreaterThanAndWidthLessThan(float length, float width);

    // tag::findByHeightBetween[]
    List<Package> findByHeightBetween(float minHeight, float maxHeight);
    // end::findByHeightBetween[]
    // end::query-by-method[]
    // tag::annotations[]

    // tag::Find[]
    @Find
    // end::Find[]
    // tag::getPackagesArrivingIn[]
    // tag::By[]
    List<Package> getPackagesArrivingIn(@By("destination") String destination);
    // end::By[]
    // end::getPackagesArrivingIn[]

    // tag::Insert[]
    @Insert
    // end::Insert[]
    // tag::add[]
    void add(Package p);
    // end::add[]

    @Find
    // tag::OrderBy[]
    @OrderBy("height")
    // end::OrderBy[]
    // tag::sortedByHeightAscending[]
    List<Package> sortedByHeightAscending();
    // end::sortedByHeightAscending[]
    // end::annotations[]
    // tag::sorting[]

    @Find
    // tag::Sort[]
    List<Package> sorted(Sort<?> sortBy);
    // end::Sort[]

    @Find
    @OrderBy("length")
    // tag::Limit[]
    List<Package> shortestWithLimit(Limit limit);
    // end::Limit[]

    @Find
    // tag::Page[]
    // tag::PageRequest[]
    Page<Package> all(PageRequest pageRequest);
    // end::PageRequest[]
    // end::Page[]
    // end::sorting[]
    // tag::query-anno[]

    @Query("WHERE length > :threshold OR height > :threshold OR width > :threshold")
    List<Package> withDimensionLargerThan(float threshold);

    @Query("WHERE length + width + height > ?1")
    List<Package> withTotalDimensionOver(float threshold);
    // end::query-anno[]

}
