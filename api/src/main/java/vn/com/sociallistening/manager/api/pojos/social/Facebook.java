package vn.com.sociallistening.manager.api.pojos.social;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.sociallistening.manager.api.services.*;
import vn.com.sociallistening.manager.entity.mongodb.*;

import java.util.*;
import java.util.stream.Collectors;

@Service("facebook")
@Slf4j
public class Facebook implements SocialNetwork {

    @Autowired
    private ContactInfoService contactInfoService;
    @Autowired
    private MembersService membersService;
    @Autowired
    private FanpageService fanpageService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private FamilyService familyService;
    @Autowired
    private LikesService likesService;
    @Autowired
    private FriendService friendService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private EducationService educationService;
    @Autowired
    private WorkExperienceService workExperienceService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;

    private List<String> listTemp;
    private String stringTemp;

    @Override
    public List<Object> dataFilter(Map<Object, String> data) {
        Map<String, Object> map;
        List<Object> converted = new ArrayList<>();

        for (Object item : data.keySet()) {
            map = (Map<String, Object>) item;
            if (data.get(item) != null) {
                switch (data.get(item).toLowerCase()) {
                    case Fanpage.FANPAGE: {
                        stringTemp = (String) map.get("facebookId");
                        stringTemp = stringTemp == null ? null : stringTemp.substring(stringTemp.indexOf("/more/") + 6, stringTemp.indexOf("/?"));
                        map.put("facebookId", stringTemp);

                        stringTemp = (String) map.get("facebookUrl");
                        map.put("facebookUrl", stringTemp.substring(0, stringTemp.indexOf("/photo")));
                        converted.add(fanpageService.objectToEntity(map));
                        break;
                    }

                    case ContactInfo.CONTACT_INFO_PROFILE: {
                        listTemp = (List<String>) map.get("socialLinks");
                        if (listTemp != null)
                            map.put("socialLinks", listTemp.stream().map(value -> value.split("\n", 2))
                                    .map(value -> value[0] + ": " + value[1])
                                    .collect(Collectors.toList()));
                        converted.add(contactInfoService.objectToEntity(map));
                        break;
                    }

                    case ContactInfo.CONTACT_INFO_FANPAGE: {
                        converted.add(contactInfoService.objectToEntity(map));
                        break;
                    }

                    case Comment.COMMENT_GROUP:
                    case Comment.COMMENT_PROFILE:
                    case Comment.COMMENT_FANPAGE: {
                        listTemp = (List<String>) map.get("parentCommentId");
                        if (listTemp != null) {
                            listTemp.replaceAll((value) -> {
                                if (value != null) {
                                    value = value.substring(0, value.indexOf("&reaction_comment_id"));
                                    return value.substring(value.lastIndexOf('_') + 1);
                                }
                                return null;
                            });
                            map.put("parentCommentId", listTemp);
                        }

                        listTemp = (List<String>) map.get("facebookOwnerId");
                        if (listTemp != null) {
                            listTemp.replaceAll(value ->
                                    value.substring(value.indexOf("feed_story_ring") + "feed_story_ring".length()));
                            map.put("facebookOwnerId", listTemp);
                        }

                        stringTemp = (String) map.get("postId");
                        stringTemp = stringTemp.substring(stringTemp.lastIndexOf(':') + 1, stringTemp.lastIndexOf('}'));
                        map.put("postId", stringTemp.contains("\"") ?
                                stringTemp.substring(0, stringTemp.lastIndexOf("\"")) :
                                stringTemp);

                        listTemp = (List<String>) map.get("contentImage");
                        if (listTemp != null) {
                            listTemp.replaceAll((value) -> value == null ? null :
                                    value.substring(value.indexOf("http"), value.indexOf("\")")));
                            map.put("contentImage", listTemp);
                        }

                        listTemp = (List<String>) map.get("commentDate");
                        if (listTemp != null) {
                            listTemp.replaceAll(value ->
                                    value == null ? null : Utils.handlePostDate(value)
                            );
                            map.put("commentDate", listTemp);
                        }
                        converted.add(commentService.objectToEntity(map));
                        break;
                    }

                    case Group.GROUP: {
                        stringTemp = (String) map.get("facebookId");
                        stringTemp = stringTemp == null ? null : stringTemp.substring(stringTemp.indexOf("/groups/") + 8, stringTemp.indexOf("?view"));
                        map.put("facebookId", stringTemp);

                        stringTemp = (String) map.get("facebookUrl");
                        map.put("facebookUrl", stringTemp.substring(0, stringTemp.indexOf("?view")));
                        converted.add(groupService.objectToEntity(map));
                        break;
                    }

                    case Members.MEMBER_GROUP: {
                        listTemp = (List<String>) map.get("facebookId");
                        if (listTemp != null) {
                            listTemp.replaceAll((value) -> value == null ?
                                    null :
                                    value.substring(value.indexOf("?id=") + 4, value.indexOf("&hf")));
                            map.put("facebookId", listTemp);
                        }
                        listTemp = (List<String>) map.get("memberId");
                        if (listTemp != null) {
                            listTemp.replaceAll((value) -> value == null ?
                                    null :
                                    value.substring(value.lastIndexOf('_') + 1));
                            map.put("memberId", listTemp);
                        }
                        listTemp = (List<String>) map.get("joinDate");
                        if (listTemp != null) {
                            listTemp.replaceAll(value ->
                                    value == null ? null : Utils.handlePostDate(value)
                            );
                            map.put("joinDate", listTemp);
                        }
                        converted.add(membersService.objectToEntity(map));
                        break;
                    }

                    case Post.POST_FANPAGE:
                    case Post.POST_PROFILE:
                    case Post.POST_GROUP: {
                        listTemp = (List<String>) map.get("feedOwnerFacebookId");
                        if (listTemp != null) {
                            listTemp.replaceAll(value -> value.substring(value.indexOf("feed_story_ring") + "feed_story_ring".length()));
                            map.put("feedOwnerFacebookId", listTemp);
                        }
                        listTemp = (List<String>) map.get("commentCount");
                        if (listTemp != null) {
                            listTemp.replaceAll((value) -> value == null ?
                                    null : value.substring(0, value.indexOf(" ")));
                            map.put("commentCount", listTemp);
                        }
                        listTemp = (List<String>) map.get("shareCount");
                        if (listTemp != null) {
                            listTemp.replaceAll((value) -> value == null ?
                                    null : value.substring(0, value.indexOf(" ")));
                            map.put("shareCount", listTemp);
                        }
                        listTemp = (List<String>) map.get("postId");
                        if (listTemp != null) {
                            listTemp.replaceAll((value) -> {
                                value = value.substring(value.lastIndexOf(':') + 1, value.indexOf('}'));
                                if (value.contains("\""))
                                    return value.substring(0, value.lastIndexOf("\""));
                                return value;
                            });
                            map.put("postId", listTemp);
                        }
                        listTemp = (List<String>) map.get("postDate");
                        if (listTemp != null) {
                            listTemp.replaceAll(value ->
                                    value == null ? null : Utils.handlePostDate(value)
                            );
                            map.put("postDate", listTemp);
                        }
                        converted.add(postService.objectToEntity(map));
                        break;
                    }

                    case Family.FAMILY: {
                        converted.add(familyService.objectToEntity(map));
                        break;
                    }

                    case Likes.LIKES: {
                        listTemp = (List<String>) map.get("facebookId");
                        if (listTemp != null) {
                            listTemp.replaceAll((value) -> value == null ?
                                    null : value.substring(value.indexOf("&id=") + 4, value.indexOf("&origin")));
                            map.put("facebookId", listTemp);
                        }
                        converted.add(likesService.objectToEntity(map));
                        break;
                    }

                    case Friend.FRIEND: {
                        listTemp = (List<String>) map.get("facebookId");
                        if (listTemp != null) {
                            listTemp.replaceAll((value) -> value == null ?
                                    null : value.substring(value.indexOf("?id=") + 4, value.indexOf("&hf")));
                            map.put("facebookId", listTemp);
                        }
                        converted.add(friendService.objectToEntity(map));
                        break;
                    }

                    case Profile.PROFILE: {
                        stringTemp = (String) map.get("facebookId");
                        stringTemp = stringTemp == null ?
                                null : stringTemp.substring(stringTemp.indexOf("?bid=") + 5, stringTemp.indexOf("&ret"));
                        map.put("facebookId", stringTemp);

                        stringTemp = (String) map.get("facebookUrl");
                        // /profile.php?id=100009011780018&v=timeline&lst=100029197630850%3A100009011780018%3A1624424478&refid=17
                        // /yeu.lacuoi.7399?v=timeline&lst=100029197630850%3A100005964750343%3A1624424756
                        map.put("facebookUrl", stringTemp.substring(0, stringTemp.indexOf("&v=") != -1 ?
                                stringTemp.indexOf("&v=") :
                                stringTemp.indexOf("?v=")));

                        listTemp = (List<String>) map.get("lifeEvents");
                        if (listTemp != null)
                            map.put("lifeEvents", listTemp.stream()
                                    .map(value -> value.split("\n", 2))
                                    .collect(Collectors.toMap(value -> value[0],
                                            value -> Arrays.stream(value[1].split("\n"))
                                                    .collect(Collectors.toList()))));

                        listTemp = (List<String>) map.get("otherNames");
                        if (listTemp != null)
                            map.put("otherNames", listTemp.stream().map(value -> value.split("\n", 2))
                                    .map(value -> value[0] + ": " + value[1])
                                    .collect(Collectors.toList()));

                        listTemp = (List<String>) map.get("placesLived");
                        if (listTemp != null)
                            map.put("placesLived", listTemp.stream().map(value -> value.split("\n", 2))
                                    .map(value -> value[0] + ": " + value[1])
                                    .collect(Collectors.toList()));

                        converted.add(profileService.ObjectToEntity(map));
                        break;
                    }

                    case Education.EDUCATION: {
                        listTemp = (List<String>) map.get("info");
                        if (listTemp != null) {
                            List<List<String>> infoList = new ArrayList<>();
                            listTemp.forEach((value) -> {
                                String[] arr = value.split("\n");
                                infoList.add(Arrays.asList(arr));
                            });
                            map.put("info", infoList);
                        }
                        converted.add(educationService.objectToEntity(map));
                        break;
                    }

                    case WorkExperience.WORK: {
                        listTemp = (List<String>) map.get("info");
                        if (listTemp != null) {
                            List<List<String>> infoList = new ArrayList<>();
                            listTemp.forEach((value) -> {
                                String[] arr = value.split("\n");
                                infoList.add(Arrays.asList(arr));
                            });
                            map.put("info", infoList);
                        }
                        converted.add(workExperienceService.objectToEntity(map));
                        break;
                    }

                    default: {
                        log.warn("UnKnow className {}", data.get(item).toLowerCase());
                        throw new UnsupportedOperationException("UnKnow className");
                    }
                }
            }
        }
        return converted;
    }

    @Override
    public void saveCrawlProfile(List<Object> converted, String typeConvert) {
        switch (typeConvert.toLowerCase()) {
            case Fanpage.FANPAGE: {
                Fanpage fanpage = Optional.ofNullable(fanpageService.mapper(converted))
                        .orElseThrow(NullPointerException::new);
                fanpageService.save(fanpage);
                break;
            }

            case Profile.PROFILE: {
                Profile profile = Optional.ofNullable(profileService.mapper(converted))
                        .orElseThrow(NullPointerException::new);
                profileService.save(profile);
                break;
            }

            case Group.GROUP: {
                Group group = Optional.ofNullable(groupService.mapper(converted))
                        .orElseThrow(NullPointerException::new);
                groupService.save(group);
                break;
            }

            default: {
                log.warn("UnKnow type convert {}", typeConvert);
                throw new UnsupportedOperationException("UnKnow type convert");
            }
        }
    }
}
