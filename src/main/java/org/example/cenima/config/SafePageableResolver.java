package org.example.cenima.config;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Set;

/**
 * Wraps the default PageableHandlerMethodArgumentResolver to enforce:
 * 1) A maximum page size (prevents ?size=999999 DoS)
 * 2) A whitelist of allowed sort fields (prevents sort injection)
 */
public class SafePageableResolver extends PageableHandlerMethodArgumentResolver {
    private static final int MAX_PAGE_SIZE = 100;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "titre", "nom", "name", "dateProjection", "prix",
            "dateSortie", "numero", "duree", "realisateur",
            "projectionId", "filmId", "villeId", "cinemaId", "salleId",
            "placeId", "nomClient", "codePayement", "reservee"
    );

    @Override
    public Pageable resolveArgument(MethodParameter methodParameter,
                                    ModelAndViewContainer mavContainer,
                                    NativeWebRequest webRequest,
                                    WebDataBinderFactory binderFactory) {
        Pageable pageable = super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        int size = Math.min(pageable.getPageSize(), MAX_PAGE_SIZE);

        Sort safeSort = Sort.unsorted();
        if (pageable.getSort().isSorted()) {
            safeSort = Sort.by(
                    pageable.getSort().stream()
                            .filter(order -> ALLOWED_SORT_FIELDS.contains(order.getProperty()))
                            .toList()
            );
        }

        return PageRequest.of(pageable.getPageNumber(), size, safeSort);
    }
}
