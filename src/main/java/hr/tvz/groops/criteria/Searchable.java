package hr.tvz.groops.criteria;

import hr.tvz.groops.model.*;
import hr.tvz.groops.repository.*;
import org.jetbrains.annotations.NotNull;

import javax.persistence.EntityNotFoundException;

import static hr.tvz.groops.constants.CrudExceptionEnum.*;

public interface Searchable {

    default @NotNull Post findPostById(@NotNull Long id, @NotNull PostRepository postRepository) {
        return postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(POST_NOT_FOUND_BY_ID.getMessageComposed(id)));
    }
    default @NotNull Role findRoleEntityById(@NotNull Long id, @NotNull RoleRepository roleRepository) {
        return roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(ROLE_NOT_FOUND_BY_ID.getMessageComposed(id)));
    }

    default @NotNull Permission findPermissionEntityById(@NotNull Long id, @NotNull PermissionRepository permissionRepository) {
        return permissionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(PERMISSION_NOT_FOUND_BY_ID.getMessageComposed(id)));
    }

    default @NotNull User findUserEntityByIdLockByPessimisticWrite(@NotNull Long id, @NotNull UserRepository userRepository) {
        return userRepository.findByIdLockByPessimisticWrite(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_BY_ID.getMessageComposed(id)));
    }

    default @NotNull User findUserEntityById(@NotNull Long id, @NotNull UserRepository userRepository) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_BY_ID.getMessageComposed(id)));
    }

    default @NotNull Comment findCommentEntityById(@NotNull Long id, @NotNull CommentRepository commentRepository) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(COMMENT_NOT_FOUND_BY_ID.getMessageComposed(id)));
    }

    default @NotNull User findUserEntityByUsernameLockByPessimisticWrite(@NotNull String username, @NotNull UserRepository userRepository) {
        return userRepository.findByUsernameLockByPessimisticWrite(username)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_BY_USERNAME.getMessageComposed(username)));
    }

    default @NotNull User findUserEntityByUsername(@NotNull String username, @NotNull UserRepository userRepository) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_BY_USERNAME.getMessageComposed(username)));
    }

    default @NotNull Group findGroupByIdLockByPessimisticWrite(@NotNull Long id, @NotNull GroupRepository groupRepository) {
        return groupRepository.findByIdLockByPessimisticWrite(id).orElseThrow(() -> new EntityNotFoundException(GROUP_NOT_FOUND_BY_ID.getMessageComposed(id)));
    }

    default @NotNull Group findGroupById(@NotNull Long id, @NotNull GroupRepository groupRepository) {
        return groupRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(GROUP_NOT_FOUND_BY_ID.getMessageComposed(id)));
    }

    default @NotNull GroupRequest findGroupRequestByGroupAndUser(@NotNull Group group, @NotNull User user, GroupRequestRepository groupRequestRepository) {
        return groupRequestRepository.findByGroupAndUser(group, user).orElseThrow(() -> new EntityNotFoundException(GROUP_NOT_FOUND_BY_ID.getMessageComposed(group.getId(), user.getId())));
    }

    default @NotNull UserGroup findUserGroupByUserAndGroup(@NotNull User user, @NotNull Group group, @NotNull UserGroupRepository userGroupRepository) {
        return userGroupRepository.findByUserAndGroup(user, group).orElseThrow(() -> new EntityNotFoundException(USER_GROUP_NOT_FOUND_BY_USER_ID_AND_GROUP_ID.getMessageComposed(user.getId(), group.getId())));
    }

    default @NotNull UserGroupRole findUserGroupRoleByUserGroupAndRole(UserGroup userGroup, Role role, UserGroupRoleRepository userGroupRoleRepository) {
        return userGroupRoleRepository.findByUserGroupAndRole(userGroup, role).orElseThrow(() -> new EntityNotFoundException(USER_GROUP_ROLE_NOT_FOUND_BY_USER_GROUP_AND_ROLE.getMessageComposed(userGroup.getId(), role.getId())));
    }

    default @NotNull PostLike findPostLikeByPostAndUser(Post post, User user, PostLikeRepository postLikeRepository) {
        return postLikeRepository.findPostLikeByPostAndUser(post, user).orElseThrow(() -> new EntityNotFoundException(POST_LIKE_NOT_FOUND_BY_POST_AND_USER.getMessageComposed(post.getId(), user.getId())));
    }

    default @NotNull FriendRequest findFriendRequestBySenderAndRecipient(User sender, User recipient, FriendRequestRepository friendRequestRepository) {
        return friendRequestRepository.findBySenderAndRecipient(sender, recipient).orElseThrow(() -> new EntityNotFoundException(FRIEND_REQUEST_NOT_FOUND_BY_SENDER_AND_RECIPIENT.getMessageComposed(sender.getId(), recipient.getId())));
    }


}
