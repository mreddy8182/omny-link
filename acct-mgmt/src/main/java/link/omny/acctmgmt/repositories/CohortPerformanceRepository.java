/*******************************************************************************
 *Copyright 2015-2018 Tim Stephenson and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package link.omny.acctmgmt.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import link.omny.custmgmt.model.Contact;
import link.omny.custmgmt.model.ContactExcept;

@RepositoryRestResource(excerptProjection = ContactExcept.class, path = "/cohorts")
public interface CohortPerformanceRepository extends CrudRepository<Contact, Long> {

    @Query("SELECT c FROM Contact c LEFT JOIN c.account a WHERE c.tenantId = :tenantId AND (stage IS NULL OR stage != 'deleted') ORDER BY c.lastUpdated DESC")
    List<Contact> findAllForTenant(@Param("tenantId") String tenantId);

    @Query("SELECT c FROM Contact c LEFT JOIN c.account a WHERE c.tenantId = :tenantId AND (stage IS NULL OR stage != 'deleted') AND (owner = :userId) ORDER BY c.lastUpdated DESC")
    List<Contact> findAllForTenantOwnedByUser(
            @Param("tenantId") String tenantId, @Param("userId") String userId);

    @Query("SELECT c FROM Contact c LEFT JOIN c.account a WHERE c.tenantId = :tenantId AND c.doNotEmail = false AND (stage IS NULL OR stage != 'deleted') ORDER BY c.lastUpdated DESC")
    List<Contact> findAllEmailableForTenant(@Param("tenantId") String tenantId);

    @Query("SELECT c FROM Contact c LEFT JOIN c.account a WHERE c.tenantId = :tenantId AND (stage IS NULL OR stage != 'deleted') ORDER BY c.lastUpdated DESC")
    List<Contact> findPageForTenant(@Param("tenantId") String tenantId,
            Pageable pageable);

    @Query("SELECT c FROM Contact c LEFT JOIN c.account a WHERE c.tenantId = :tenantId AND c.doNotEmail = false AND (stage IS NULL OR stage != 'deleted') ORDER BY c.lastUpdated DESC")
    List<Contact> findEmailablePageForTenant(@Param("tenantId") String tenantId,
            Pageable pageable);

    List<Contact> findByLastName(@Param("lastName") String lastName);

    // This applies AND semantics
    List<Contact> findByFirstNameOrLastName(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName);

    // This appears to match everything :-(
    List<Contact> findByFirstNameOrLastNameOrAccountName(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("account.name") String accountName);

    @Query("SELECT c FROM Contact c "
            + "WHERE c.firstName = :firstName AND c.lastName = :lastName ")
    // TODO ought to restrict this to single tenant
    List<Contact> findByFirstNameAndLastName(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName);

    @Query("SELECT c FROM Contact c "
            + "WHERE c.firstName = :firstName AND c.lastName = :lastName "
            + "AND c.account.name = :accountName")
    // TODO ought to restrict this to single tenant
    List<Contact> findByFirstNameLastNameAndAccountName(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("accountName") String accountName);

    @Query("SELECT c FROM Contact c WHERE c.email = :email AND (stage IS NULL OR stage != 'deleted') AND c.tenantId = :tenantId")
    List<Contact> findByEmail(@Param("email") String email,
            @Param("tenantId") String tenantId);

    @Query("SELECT c FROM Contact c WHERE c.tags LIKE :tag AND (stage IS NULL OR stage != 'deleted') AND c.tenantId = :tenantId")
    List<Contact> findByTag(@Param("tag") String tag,
            @Param("tenantId") String tenantId);

    @Query("SELECT c.stage, COUNT(c) FROM Contact c WHERE (stage IS NULL OR stage != 'deleted') AND c.tenantId = :tenantId GROUP BY c.stage")
    List<Object[]> findAllForTenantGroupByStage(
            @Param("tenantId") String tenantId);

    @Query("SELECT c FROM Contact c WHERE c.uuid = :uuid AND (stage IS NULL OR stage != 'deleted') AND c.tenantId = :tenantId")
    List<Contact> findByUuid(@Param("uuid") String uuid,
            @Param("tenantId") String tenantId);

    @Query("SELECT c FROM Contact c WHERE c.uuid = :uuid AND c.email IS NOT NULL AND (stage IS NULL OR stage != 'deleted') AND c.tenantId = :tenantId")
    List<Contact> findKnownByUuid(@Param("uuid") String uuid,
            @Param("tenantId") String tenantId);

    @Query("SELECT c FROM Contact c WHERE c.uuid = :uuid AND c.firstName IS NULL AND c.lastName IS NULL AND (stage IS NULL OR stage != 'deleted') AND c.tenantId = :tenantId")
    List<Contact> findAnonByUuid(@Param("uuid") String uuid,
            @Param("tenantId") String tenantId);

    @Query("SELECT c FROM Contact c INNER JOIN c.activities a WHERE a.occurred > :sinceDate AND (stage IS NULL OR stage != 'deleted') AND c.tenantId = :tenantId ORDER BY a.occurred DESC")
    List<Contact> findActiveForTenant(@Param("sinceDate") Date sinceDate,
            @Param("tenantId") String tenantId);

    @Query(value = "UPDATE OL_CONTACT c set c.account_id = :accountId WHERE c.id = :contactId", nativeQuery = true)
    @Modifying(clearAutomatically = true)
    public void setAccount(@Param("contactId") Long contactId, @Param("accountId") Long accountId);

    @Override
    @Query("UPDATE #{#entityName} x set x.stage = 'deleted' where x.id = :id")
    @Modifying(clearAutomatically = true)
    public void delete(@Param("id") Long id);

}
