package hr.tvz.groops.controller;

import hr.tvz.groops.command.crud.GroupCommand;
import hr.tvz.groops.command.crud.RoleCommand;
import hr.tvz.groops.command.search.GroupSearchCommand;
import hr.tvz.groops.dto.response.GroupDto;
import hr.tvz.groops.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController extends ControllerBase {
    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/{id}")
    GroupDto findGroupById(@PathVariable("id") Long id) {
        return groupService.findById(id);
    }

    @GetMapping
    List<GroupDto> findAll() {
        return groupService.findAll();
    }

    @PostMapping
    GroupDto create(@RequestBody @Valid GroupCommand command) {
        return groupService.create(command);
    }

    @PutMapping("/{id}")
    GroupDto update(@PathVariable("id") Long id, @RequestBody @Valid GroupCommand command) {
        return groupService.update(id, command);
    }

    @PutMapping("/{groupId}/request/user/{userId}")
    void acceptGroupRequest(
            @PathVariable("groupId") Long groupId,
            @PathVariable("userId") Long userId,
            @RequestBody @Valid RoleCommand command) {
        groupService.acceptGroupRequest(userId, groupId, command.getRole());
    }

    @DeleteMapping("/{groupId}/request/user/{userId}")
    void rejectGroupRequest(
            @PathVariable("groupId") Long groupId,
            @PathVariable("userId") Long userId
    ) {
        groupService.rejectGroupRequest(userId, groupId);
    }

    @PostMapping("/{groupId}/request")
    void sendGroupJoinRequest(@PathVariable("groupId") Long groupId) {
        groupService.sendGroupJoinRequest(groupId);
    }

    @PostMapping("/search")
    Page<GroupDto> searchGroup(@RequestBody GroupSearchCommand command) {
        return groupService.search(command, command.getPageable());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteGroupById(@PathVariable("id") Long id) {
        groupService.deleteById(id);
    }

    @DeleteMapping("/{groupId}/user/{userId}")
    void kickUser(@PathVariable("groupId") Long groupId, @PathVariable("userId") Long userId) {
        groupService.kickUser(groupId, userId);
    }

    @PutMapping("/{groupId}/user/{userId}")
    void changeUserRole(@PathVariable("groupId") Long groupId,
                        @PathVariable("userId") Long userId,
                        @RequestBody @Valid RoleCommand command) {
        groupService.changeUserRole(groupId, userId, command.getRole());
    }

    @DeleteMapping("/{groupId}")
    void leaveGroup(@PathVariable("groupId") Long groupId) {
        groupService.leaveGroup(groupId);
    }

}
