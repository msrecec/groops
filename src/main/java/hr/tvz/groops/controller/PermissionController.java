package hr.tvz.groops.controller;

import hr.tvz.groops.command.crud.PermissionCommand;
import hr.tvz.groops.command.search.PermissionSearchCommand;
import hr.tvz.groops.dto.response.PermissionDto;
import hr.tvz.groops.service.impl.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionController extends ControllerBase {
    private final PermissionService permissionService;

    PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping
    List<PermissionDto> findAllPermissions() {
        return permissionService.findAll();
    }

    @GetMapping("/{id}")
    PermissionDto findPermissionById(@PathVariable("id") Long id) {
        return permissionService.findById(id);
    }

    @PostMapping
    PermissionDto createPermission(@RequestBody @Valid PermissionCommand command) {
        return permissionService.create(command);
    }

    @PutMapping("/{id}")
    PermissionDto updatePermission(@RequestBody @Valid PermissionCommand command, @PathVariable("id") Long id) {
        return permissionService.update(command, id);
    }

    @PostMapping("/search")
    Page<PermissionDto> searchPermission(@RequestBody PermissionSearchCommand command) {
        return permissionService.search(command, command.getPageable());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deletePermissionById(@PathVariable("id") Long id) {
        permissionService.deleteById(id);
    }
}
