package hr.tvz.groops.config;

import hr.tvz.groops.dto.response.GroupDto;
import hr.tvz.groops.dto.response.UserDto;
import hr.tvz.groops.model.Group;
import hr.tvz.groops.model.User;
import hr.tvz.groops.service.s3.S3Service;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    private final S3Service s3Service;

    @Autowired
    public ModelMapperConfig(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @Bean("modelMapper")
    ModelMapper getModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        addTypeMappingForUser(modelMapper);
        addTypeMappingForGroup(modelMapper);
        return modelMapper;
    }

    private void addTypeMappingForUser(ModelMapper modelMapper) {
        TypeMap<User, UserDto> propertyMapper = modelMapper.createTypeMap(User.class, UserDto.class);
        propertyMapper.addMappings(m -> {
            m.using(getProfilePictureConverter()).map(User::getProfilePictureKey, UserDto::setProfilePictureDownloadLink);
        });
    }

    private void addTypeMappingForGroup(ModelMapper modelMapper) {
        TypeMap<Group, GroupDto> propertyMapper = modelMapper.createTypeMap(Group.class, GroupDto.class);
        propertyMapper.addMappings(m -> {
            m.using(getProfilePictureConverter()).map(Group::getProfilePictureKey, GroupDto::setProfilePictureDownloadLink);
        });
    }

    private AbstractConverter<String, String> getProfilePictureConverter() {
        return new AbstractConverter<>() {
            @Override
            protected String convert(String source) {
                return s3Service.generateDownloadLinkByObjectKey(source);
            }
        };
    }

}
