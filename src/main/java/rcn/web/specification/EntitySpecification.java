package rcn.web.specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.Specification;

import rcn.web.model.Area;

@Configuration
public class EntitySpecification<E> {

	@SuppressWarnings("serial")
	public static <E> Specification<E> textInAllStringColumns(String text) {//Specification<ChannelPackage>

        if (!text.contains("%")) {
            text = "%"+text+"%";
        }
        final String finalText = text;

        return new Specification<E>() {
            @Override
            public Predicate toPredicate(Root<E> root, CriteriaQuery<?> cq, CriteriaBuilder builder) {
                return builder.or(root.getModel().getDeclaredSingularAttributes().stream().filter(a-> {
                    if (a.getJavaType().getSimpleName().equalsIgnoreCase("string")) {
                        return true;
                    }
                    else {
                        return false;
                }}).map(a -> builder.like(builder.lower(root.get(a.getName())), finalText.toLowerCase())
                    ).toArray(Predicate[]::new)
                );
            }
        };
    }
	
	@SuppressWarnings("serial")
	public static <E> Specification<E> textInAllStringColumnsCaseSensitive(String text) {//Specification<ChannelPackage>

        if (!text.contains("%")) {
            text = "%"+text+"%";
        }
        final String finalText = text;

        return new Specification<E>() {
            @Override
            public Predicate toPredicate(Root<E> root, CriteriaQuery<?> cq, CriteriaBuilder builder) {
                return builder.or(root.getModel().getDeclaredSingularAttributes().stream().filter(a-> {
                    if (a.getJavaType().getSimpleName().equalsIgnoreCase("string")) {
                        return true;
                    }
                    else {
                        return false;
                }}).map(a -> builder.like(root.get(a.getName()), finalText)
                    ).toArray(Predicate[]::new)
                );
            }
        };
    }
    
    public static <E> Specification<E> filterArea(String area) {
    	return (root, query, criteriaBuilder) -> {
            Join<Area, E> authorsBook = root.join("area");
            return criteriaBuilder.equal(authorsBook.get("name"), area);
        };
    }

 }