package vn.com.sociallistening.manager.api.services;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import vn.com.sociallistening.manager.api.configurations.CustomAbstractMongoEvent;
import vn.com.sociallistening.manager.api.constraints.mongo.CascadeDeleteCallback;
import vn.com.sociallistening.manager.api.constraints.mongo.CascadeSaveCallback;
import vn.com.sociallistening.manager.api.constraints.mongo.ImportValueCallback;
import vn.com.sociallistening.manager.api.repository.mariadb.ProfileRepository;
import vn.com.sociallistening.manager.entity.mongodb.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProfileService extends CustomAbstractMongoEvent<Profile> {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private MongoOperations mongoOperations;

    private List<Profile> deleteProfiles = new ArrayList<>();

    public void save(Profile profile) {
        profileRepository.save(profile);
    }

    public boolean existsByFacebookUrlContaining(String url) {
        return profileRepository.existsByFacebookUrlContaining(url);
    }

    public boolean existsByFacebookId(String id) {
        return profileRepository.existsByFacebookId(id);
    }

    public Profile ObjectToEntity(Map<String, Object> map) {
        try {
            Profile profile = new Profile();

            profile.setFacebookId((String) map.get("facebookId"));
            profile.setFacebookUrl((String) map.get("facebookUrl"));
            profile.setCoverImage((String) map.get("coverImage"));
            profile.setAvatarImage((String) map.get("avatarImage"));
            profile.setName((String) map.get("name"));
            profile.setGender((String) map.get("gender"));
            profile.setAbout((String) map.get("about"));
            profile.setBirthDay((String) map.get("birthDay"));
            profile.setInterestedIn((String) map.get("interestedIn"));
            profile.setLanguages((String) map.get("languages"));
            profile.setQuote((String) map.get("quote"));
            profile.setMaritalStatus((String) map.get("maritalStatus"));
            profile.setReligiousViews((String) map.get("religiousViews"));
            profile.setPoliticalViews((String) map.get("politicalViews"));
            profile.setOtherNames((List<String>) map.get("otherNames"));
            profile.setLifeEvents((Map<String, Object>) map.get("lifeEvents"));
            profile.setPlacesLived((List<String>) map.get("placesLived"));

            return profile;
        } catch (Exception ex) {
            log.warn("convert profile fail {}", ex);
            return null;
        }
    }

    public Profile mapper(List<Object> data) {

        Profile profile = null;
        List<Friend> friends = new ArrayList<>();
        List<Family> families = new ArrayList<>();
        List<Likes> likes = new ArrayList<>();
        ContactInfo contact = null;
        List<Education> educations = new ArrayList<>();
        List<WorkExperience> workExperiences = new ArrayList<>();
        List<Post> posts = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();
        for (Object item : data) {
            if (item instanceof List) {
                if (!((List) item).isEmpty() && ((List) item).get(0) instanceof Post)
                    posts.addAll((List<Post>) item);
                else if (!((List) item).isEmpty() && ((List) item).get(0) instanceof Comment)
                    comments.addAll((List<Comment>) item);
                else if (!((List) item).isEmpty() && ((List) item).get(0) instanceof Friend)
                    friends.addAll((List<Friend>) item);
                else if (!((List) item).isEmpty() && ((List) item).get(0) instanceof Likes)
                    likes.addAll((List<Likes>) item);
                else if (!((List) item).isEmpty() && ((List) item).get(0) instanceof Education)
                    educations.addAll((List<Education>) item);
                else if (!((List) item).isEmpty() && ((List) item).get(0) instanceof WorkExperience)
                    workExperiences.addAll((List<WorkExperience>) item);
                else if (!((List) item).isEmpty() && ((List) item).get(0) instanceof Family)
                    families.addAll((List<Family>) item);
            } else if (item instanceof Profile)
                profile = (Profile) item;
            else if (item instanceof ContactInfo)
                contact = (ContactInfo) item;
        }
        profile.setFriends(friends);
        profile.setFamilies(families);
        profile.setLikes(likes);
        profile.setContactInfo(contact);
        profile.setEducations(educations);
        profile.setWorkExperiences(workExperiences);
        /**
         * add comment to post
         */
        if (!posts.isEmpty() && !comments.isEmpty())
            posts.forEach((value) ->
                    value.setComments(commentService.getSubComment(comments, value.getPostId())));
        profile.setPosts(posts);
        return profile;
    }


    @SneakyThrows
    @Override
    public void onBeforeConvert(BeforeConvertEvent<Profile> event) {
        Profile profile = event.getSource();
        ReflectionUtils.doWithFields(profile.getClass(), new ImportValueCallback(profile));
        ReflectionUtils.doWithFields(profile.getClass(), new CascadeSaveCallback(profile, mongoOperations));
    }

    @Override
    public void onAfterDelete(AfterDeleteEvent<Profile> event) {
        deleteProfiles.addAll((Collection<? extends Profile>) this.getObject(event.getDocument()));
    }

    @Override
    public void onBeforeDelete(BeforeDeleteEvent<Profile> event) {
        deleteProfiles.forEach(value -> ReflectionUtils.doWithFields(Profile.class, new CascadeDeleteCallback(value, mongoOperations)));
    }
}
