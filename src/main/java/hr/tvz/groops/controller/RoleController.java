package hr.tvz.groops.controller;

import hr.tvz.groops.command.crud.RoleCommand;
import hr.tvz.groops.command.search.RoleSearchCommand;
import hr.tvz.groops.dto.response.RoleDto;
import hr.tvz.groops.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController extends ControllerBase {
    private final RoleService roleService;

    RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    List<RoleDto> findAllRoles() {
        return roleService.findAll();
    }

    @GetMapping("/{id}")
    RoleDto findRoleById(@PathVariable("id") Long id) {
        return roleService.findById(id);
    }

    @PutMapping("/{roleId}/permissions/{permissionId}")
    void addPermissionToRole(@PathVariable("roleId") Long roleId, @PathVariable("permissionId") Long permissionId) {
        roleService.addPermissionToRole(roleId, permissionId);
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    void deletePermissionToRole(@PathVariable("roleId") Long roleId, @PathVariable("permissionId") Long permissionId) {
        roleService.removePermissionFromRole(roleId, permissionId);
    }

    @PostMapping
    RoleDto createRole(@RequestBody @Valid RoleCommand command) {
        return roleService.create(command);
    }

    @PutMapping("/{id}")
    RoleDto updateRole(@RequestBody @Valid RoleCommand command, @PathVariable("id") Long id) {
        return roleService.update(command, id);
    }

    @PostMapping("/search")
    Page<RoleDto> searchRole(@RequestBody RoleSearchCommand command) {
        return roleService.search(command, command.getPageable());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteRoleById(@PathVariable("id") Long id) {
        roleService.deleteById(id);
    }
}
