package hr.tvz.groops.command.searchPaginated;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class PageableSearchCommand {
    private List<SortOrder> sort;
    private Integer limit = 15;
    private Integer page = 0;

    public Pageable getPageable() {

        if (sort == null) {
            Sort.Order sortOrder = new Sort.Order(Sort.Direction.fromString("DESC"), "id").nullsNative();
            return PageRequest.of(
                    page,
                    limit,
                    Sort.by(sortOrder)
            );
        }

        List<Sort.Order> sortOrders = sort.stream()
                .map(so ->
                        new Sort.Order(Sort.Direction.fromString(so.direction != null ?
                                so.direction.toString() : "DESC"), so.property != null ?
                                so.property : "id").ignoreCase().nullsNative()).collect(Collectors.toList()
                );
        return PageRequest.of(page, limit, Sort.by(sortOrders));
    }

    public void setPage(Integer page) {
        if (page == null) {
            page = 0;
        } else if (page > 0) {
            page -= 1;
        } else if (page < 0) {
            page = 0;
        }
        this.page = page;
    }

    public void setLimit(Integer limit) {
        if (limit == null) {
            limit = 15;
        }
        this.limit = limit;
    }

    static class SortOrder {
        Sort.Direction direction;
        String property;

        SortOrder(String direction, String property) {
            this.direction = Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC);
            this.property = property;
        }

        public Sort.Direction getDirection() {
            return direction;
        }

        public void setDirection(Sort.Direction direction) {
            this.direction = direction;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }
    }
}
