package hr.tvz.groops.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.tvz.groops.command.crud.*;
import hr.tvz.groops.command.search.UserSearchCommand;
import hr.tvz.groops.dto.response.FriendRequestDto;
import hr.tvz.groops.dto.response.UserDto;
import hr.tvz.groops.service.UserService;
import hr.tvz.groops.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController extends ControllerBase {
    private final UserService userService;
    private final ValidationService validationService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserController(UserService userService,
                          ValidationService validationService,
                          ObjectMapper objectMapper) {
        this.userService = userService;
        this.validationService = validationService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    List<UserDto> findAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    UserDto findUserById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }

    @GetMapping("/current")
    UserDto findCurrentUser() {
        return userService.getCurrent();
    }

    @PostMapping("/register")
    UserDto registerUser(@RequestBody @Valid UserCreateCommand command) {
        return userService.register(command);
    }

    @PostMapping("/current/upload-profile")
    UserDto uploadProfilePicture(@RequestParam("file") MultipartFile file, @RequestParam("command") MultipartFile command) throws IOException {
        UserUpdateCommand userUpdateCommand = objectMapper.readValue(command.getInputStream(), UserUpdateCommand.class);
        validationService.validate(userUpdateCommand);
        return userService.update(userUpdateCommand, file);
    }

    @PutMapping("/current")
    UserDto updateUser(@RequestBody @Valid UserUpdateCommand command) {
        return userService.update(command, null);
    }

    @PostMapping("/friend-request/send/{recipientId}")
    void sendFriendRequest(@PathVariable("recipientId") Long recipientId) {
        userService.sendFriendRequest(recipientId);
    }

    @PostMapping("/friend-request/accept/{senderId}")
    void acceptFriendRequest(@PathVariable("senderId") Long senderId) {
        userService.acceptFriendRequest(senderId);
    }

    @DeleteMapping("/friend-request/reject/{senderId}")
    void rejectFriendRequest(@PathVariable("senderId") Long senderId) {
        userService.rejectFriendRequest(senderId);
    }

    @GetMapping("/friend-request/pending/received")
    List<FriendRequestDto> findAllPendingReceivedFriendRequests() {
        return userService.findAllPendingReceivedFriendRequests();
    }

    @GetMapping("/friend-request/pending/received/{senderId}")
    FriendRequestDto findPendingReceivedFriendRequest(@PathVariable("senderId") Long senderId) {
        return userService.findPendingReceivedFriendRequest(senderId);
    }

    @GetMapping("/friend-request/pending/sent")
    List<FriendRequestDto> findAllPendingSentFriendRequests() {
        return userService.findAllPendingSentFriendRequests();
    }

    @GetMapping("/friend-request/pending/sent/{recipientId}")
    FriendRequestDto findPendingSentFriendRequest(@PathVariable("recipientId") Long recipientId) {
        return userService.findPendingSentFriendRequest(recipientId);
    }

    @PutMapping("/change-mail")
    void updateUserMail(@RequestBody @Valid EmailUpdateCommand command) {
        userService.changeMail(command.getEmail());
    }

    @PutMapping("/change-password")
    void updateUserPassword(@RequestBody @Valid PasswordUpdateCommand command) {
        userService.changePassword(command.getPassword());
    }

    @PostMapping("/search")
    Page<UserDto> searchUser(@RequestBody UserSearchCommand command) {
        return userService.search(command, command.getPageable());
    }

    @DeleteMapping("/current")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCurrentUser() {
        userService.deleteCurrent();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUserById(@PathVariable("id") Long id) {
        userService.deleteById(id);
    }

}
