package org.springframework.springfaces.traveladvisor.domain.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.springfaces.traveladvisor.domain.City;
import org.springframework.springfaces.traveladvisor.domain.HotelSummary;
import org.springframework.stereotype.Repository;

@Repository
public class HotelSummaryRepository {

	private static final String AVERAGE_REVIEW_FUNCTION = "avg(r.rating)";

	private static final String QUERY = "select new " + HotelSummary.class.getName() + "(h.city, h.name, "
			+ AVERAGE_REVIEW_FUNCTION + ") from Hotel h left outer join h.reviews r where h.city = ?1 group by h";

	private static final String COUNT_QUERY = "select count(h) from Hotel h where h.city = ?1";

	private EntityManager entityManager;

	public Page<HotelSummary> findByCity(City city, Pageable pageable) {
		StringBuilder queryString = new StringBuilder(QUERY);
		applySorting(queryString, pageable == null ? null : pageable.getSort());

		Query query = entityManager.createQuery(queryString.toString());
		query.setParameter(1, city);
		query.setFirstResult(pageable.getOffset());
		query.setMaxResults(pageable.getPageSize());

		Query countQuery = entityManager.createQuery(COUNT_QUERY);
		countQuery.setParameter(1, city);

		@SuppressWarnings("unchecked")
		List<HotelSummary> content = query.getResultList();

		Long total = (Long) countQuery.getSingleResult();

		return new PageImpl<HotelSummary>(content, pageable, total);
	}

	public static void applySorting(StringBuilder query, Sort sort) {
		if (sort != null) {
			query.append(" order by");
			for (Order order : sort) {
				String aliasedProperty = getAliasedProperty(order.getProperty());
				query.append(String.format(" %s %s,", aliasedProperty, QueryUtils.toJpaDirection(order)));
			}
			query.deleteCharAt(query.length() - 1);
		}
	}

	private static String getAliasedProperty(String property) {
		if (property.equals("averageRating")) {
			return AVERAGE_REVIEW_FUNCTION;
		}
		return "h." + property;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
