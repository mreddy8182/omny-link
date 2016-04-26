package link.omny.catalog.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import link.omny.catalog.model.GeoPoint;
import link.omny.catalog.model.StockCategory;
import link.omny.catalog.repositories.StockCategoryRepository;
import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * REST web service for accessing stock items.
 * 
 * @author Tim Stephenson
 */
@Controller
@RequestMapping(value = "/{tenantId}/stock-categories")
public class StockCategoryController {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(StockCategoryController.class);

    @Value("${omny.catalog.searchRadius:100}")
    private String searchRadius;

    private int iSearchRadius;

    @Autowired
    private StockCategoryRepository stockCategoryRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GeolocationService geo;

    public int getSearchRadius() {
        if (iSearchRadius == 0) {
            iSearchRadius = Integer.parseInt(searchRadius);
        }
        return iSearchRadius;
    }

    /**
     * Imports JSON representation of stockCategorys.
     *
     * <p>
     * This is a handy link: http://shancarter.github.io/mr-data-converter/
     *
     * @param file
     *            A file posted in a multi-part request
     * @return The meta data of the added model
     * @throws IOException
     *             If cannot parse the JSON.
     */
    @RequestMapping(value = "/uploadcsv", method = RequestMethod.POST)
    public @ResponseBody Iterable<StockCategory> handleCsvFileUpload(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "file", required = true) MultipartFile file)
            throws IOException {
        LOGGER.info(String.format("Uploading CSV stockCategorys for: %1$s",
                tenantId));

        throw new RuntimeException("Not yet implemented");

        // String content = new String(file.getBytes());
        // List<StockCategory> list = new CsvImporter().readStockCategorys(
        // new StringReader(
        // content), content.substring(0, content.indexOf('\n'))
        // .split(","));
        // LOGGER.info(String.format("  found %1$d stockCategorys",
        // list.size()));
        // for (StockCategory stockCategory : list) {
        // stockCategory.setTenantId(tenantId);
        // }
        //
        // Iterable<StockCategory> result = stockCategoryRepo.save(list);
        // LOGGER.info("  saved.");
        // return result;
    }

    /**
     * Return just the stockCategorys for a specific tenant.
     * 
     * @return stockCategorys for that tenant.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ResponseBody List<ShortStockCategory> listForTenant(
            @PathVariable("tenantId") String tenantId,
            @AuthenticationPrincipal UserDetails activeUser,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit) {
        LOGGER.info(String.format("List stockCategorys for tenant %1$s",
                tenantId));

        List<StockCategory> list;
        if (limit == null) {
            // TODO unfortunately activeUser is null, prob some kind of class
            // cast error it seems
            // Use SecurityContextHolder as temporary fallback
            Authentication authentication = SecurityContextHolder.getContext()
                    .getAuthentication();

            for (GrantedAuthority a : authentication.getAuthorities()) {
                System.out.println("  " + a.getAuthority());
                System.out.println("  "
                        + a.getAuthority().equals("ROLE_editor"));
                System.out.println("  " + a.getAuthority().equals("editor"));
            }

            // if (authentication.getAuthorities().contains("ROLE_editor")) {
            list = stockCategoryRepo.findAllForTenant(tenantId);
            // } else {
            // list = stockCategoryRepo.findAllForTenantOwnedByUser(tenantId,
            // authentication.getName());
            // }
        } else {
            Pageable pageable = new PageRequest(page == null ? 0 : page, limit);
            list = stockCategoryRepo.findPageForTenant(tenantId, pageable);
        }
        LOGGER.info(String.format("Found %1$s stockCategorys", list.size()));

        return wrap(list);
    }

    /**
     * Return Stock Categories for a specific tenant within a given distance of
     * the search location.
     * 
     * @return stockCategorys for that tenant.
     * @throws IOException
     *             If unable to contact the geocoding service or it in turn
     *             throws an exception.
     */
    @RequestMapping(value = "/findByLocation", method = RequestMethod.GET)
    public @ResponseBody List<ShortStockCategory> findByLocation(
            @PathVariable("tenantId") String tenantId,
            @RequestParam(value = "q", required = true) String q,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "limit", required = false) Integer limit)
            throws IOException {
        LOGGER.info(String.format("List stockCategories for tenant %1$s",
                tenantId));

        List<StockCategory> list;
        StockCategory exactMatch = stockCategoryRepo.findByName(q);
        if (exactMatch == null) {
            GeoPoint target = geo.locate(q);

            list = new ArrayList<StockCategory>();
            List<StockCategory> tmpList = stockCategoryRepo
                    .findAllForTenant(tenantId);
            for (StockCategory stockCategory : tmpList) {
                try {
                    if (stockCategory.getLng() == 0.0d) {
                        stockCategory.setGeoPoint(geo.locate(stockCategory
                                .getPostCode()));
                        stockCategoryRepo.save(stockCategory);
                    }

                    if (geo.distance(target, stockCategory) < getSearchRadius()) {
                        list.add(stockCategory);
                    }
                } catch (Exception e) {
                    LOGGER.error("Exception calculating distance of "
                            + stockCategory.getName() + " from " + q);
                }
            }
        } else {
            list = Collections.singletonList(exactMatch);
        }
        Collections.sort(list,
                        (o1, o2) -> ((int) o1.getDistance())
                                - ((int) o2.getDistance()));
        LOGGER.info(String.format("Found %1$s stockCategorys", list.size()));

        return wrap(list);
    }

    /**
     * Create a new stock category.
     * 
     * @return
     */
    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> create(
            @PathVariable("tenantId") String tenantId,
            @RequestBody StockCategory stockCategory,
            UriComponentsBuilder builder) {
        stockCategory.setTenantId(tenantId);
        stockCategoryRepo.save(stockCategory);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(builder.path("/stockCategorys/{id}")
                .buildAndExpand(stockCategory.getId()).toUri());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    /**
     * Update an existing stockCategory.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = { "application/json" })
    public @ResponseBody void update(@PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long stockCategoryId,
            @RequestBody StockCategory updatedStockCategory) {
        StockCategory stockCategory = stockCategoryRepo
                .findOne(stockCategoryId);

        BeanUtils.copyProperties(updatedStockCategory, stockCategory, "id");
        stockCategory.setTenantId(tenantId);
        stockCategoryRepo.save(stockCategory);
    }

    /**
     * Delete an existing stockCategory.
     */
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, consumes = { "application/json" })
    public @ResponseBody void delete(@PathVariable("tenantId") String tenantId,
            @PathVariable("id") Long stockCategoryId) {
        StockCategory stockCategory = stockCategoryRepo
                .findOne(stockCategoryId);

        stockCategoryRepo.delete(stockCategory);
    }

    private List<ShortStockCategory> wrap(List<StockCategory> list) {
        List<ShortStockCategory> resources = new ArrayList<ShortStockCategory>(
                list.size());
        for (StockCategory stockCategory : list) {
            resources.add(wrap(stockCategory));
        }
        return resources;
    }

    private ShortStockCategory wrap(StockCategory stockCategory) {
        ShortStockCategory resource = new ShortStockCategory();
        BeanUtils.copyProperties(stockCategory, resource);
        // Not set by BeanUtils due to diff type
        resource.setDistance(String.valueOf(Math.round(stockCategory
                .getDistance())));
        Link detail = linkTo(StockCategoryRepository.class,
                stockCategory.getId())
                .withSelfRel();
        resource.add(detail);
        resource.setSelfRef(detail.getHref());
        return resource;
    }

    private Link linkTo(
            @SuppressWarnings("rawtypes") Class<? extends CrudRepository> clazz,
            Long id) {
        return new Link(clazz.getAnnotation(RepositoryRestResource.class)
                .path() + "/" + id);
    }

    @Data
    public static class ShortStockCategory extends ResourceSupport {
        private String selfRef;
        private String name;
        private String postCode;
        private String distance;
        private Date created;
        private Date lastUpdated;
    }
}