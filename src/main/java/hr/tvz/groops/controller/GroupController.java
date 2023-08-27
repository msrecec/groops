package hr.tvz.groops.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.tvz.groops.command.crud.CommentCommand;
import hr.tvz.groops.command.crud.GroupCommand;
import hr.tvz.groops.command.crud.PostCommand;
import hr.tvz.groops.command.crud.RoleCommand;
import hr.tvz.groops.command.search.GroupSearchCommand;
import hr.tvz.groops.command.search.PostSearchCommand;
import hr.tvz.groops.dto.response.CommentDto;
import hr.tvz.groops.dto.response.GroupDto;
import hr.tvz.groops.dto.response.PostDto;
import hr.tvz.groops.service.CommentService;
import hr.tvz.groops.service.GroupService;
import hr.tvz.groops.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/groups")
public class GroupController extends ControllerBase {
    private final GroupService groupService;
    private final PostService postService;
    private final CommentService commentService;
    private final ObjectMapper objectMapper;

    @Autowired
    public GroupController(GroupService groupService,
                           PostService postService,
                           CommentService commentService,
                           ObjectMapper objectMapper) {
        this.groupService = groupService;
        this.postService = postService;
        this.commentService = commentService;
        this.objectMapper = objectMapper;
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
    GroupDto create(@RequestBody @Valid GroupCommand command) throws JsonProcessingException {
        return groupService.create(command);
    }

    @PostMapping("/{id}/post/media")
    PostDto createPostWithMedia(@PathVariable("id") Long id, @RequestParam("command") String commandJSON, @RequestParam("file") MultipartFile file) throws JsonProcessingException {
        PostCommand command = objectMapper.readValue(commandJSON, PostCommand.class);
        return postService.create(command, id, file);
    }

    @PostMapping("/{id}/post")
    PostDto createPostWithoutMedia(@PathVariable("id") Long id, @RequestBody @Valid PostCommand command) throws JsonProcessingException {
        return postService.create(command, id);
    }

    @PostMapping("/{id}/post/{postId}/media")
    PostDto updatePostWithMedia(@PathVariable("id") Long id, @PathVariable("postId") Long postId, @RequestParam("command") String commandJSON, @RequestParam("file") MultipartFile file) throws JsonProcessingException {
        PostCommand command = objectMapper.readValue(commandJSON, PostCommand.class);
        return postService.update(command, postId, file);
    }

    @PostMapping("/{id}/post/{postId}")
    PostDto updatePostWithoutMedia(@PathVariable("id") Long id, @RequestParam("removeMedia") Optional<Boolean> removeMedia, @PathVariable("postId") Long postId, @RequestParam("command") PostCommand command) throws JsonProcessingException {
        return postService.update(command, postId, removeMedia.orElse(false));
    }

    @DeleteMapping("/{id}/post/{postId}")
    void deletePost(@PathVariable("id") Long id, @PathVariable("postId") Long postId) {
        postService.delete(postId);
    }

    @DeleteMapping("/{id}/post/{postId}/like")
    void likePost(@PathVariable("id") Long id, @PathVariable("postId") Long postId) {
        postService.like(postId);
    }

    @DeleteMapping("/{id}/post/{postId}/unlike")
    void unlikePost(@PathVariable("id") Long id, @PathVariable("postId") Long postId) {
        postService.unlike(postId);
    }

    @PostMapping("/post/search")
    Page<PostDto> searchPost(@RequestBody PostSearchCommand command) {
        return postService.search(command, command.getPageable());
    }

    @PostMapping("/post/{id}/comment")
    CommentDto createComment(@PathVariable("id") Long id, @RequestBody @Valid CommentCommand command) {
        return commentService.create(command, id);
    }

    @PutMapping("/post/{id}/comment/{commentId}")
    CommentDto updateComment(@PathVariable("id") Long id, @PathVariable("commentId") Long commentId, @RequestBody @Valid CommentCommand command) {
        return commentService.update(command, commentId);
    }

    @DeleteMapping("/post/{id}/comment/{commentId}")
    void deleteComment(@PathVariable("id") Long id, @PathVariable("commentId") Long commentId) {
        commentService.delete(commentId);
    }

    @GetMapping("/post/{id}/comment")
    List<CommentDto> findCommentsByPostId(@PathVariable("id") Long id) {
        return commentService.findAllByPostId(id);
    }

    @PostMapping("/{id}/upload-profile")
    GroupDto uploadProfilePicture(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file) {
        return groupService.uploadProfilePicture(id, file);
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

    @DeleteMapping("/leave/{groupId}")
    void leaveGroup(@PathVariable("groupId") Long groupId) {
        groupService.leaveGroup(groupId);
    }

}
