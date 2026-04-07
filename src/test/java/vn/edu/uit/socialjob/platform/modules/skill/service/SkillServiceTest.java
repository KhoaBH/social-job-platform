package vn.edu.uit.socialjob.platform.modules.skill.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import vn.edu.uit.socialjob.platform.modules.skill.dto.CreateUserSkill;
import vn.edu.uit.socialjob.platform.modules.skill.entity.Skill;
import vn.edu.uit.socialjob.platform.modules.skill.entity.UserSkill;
import vn.edu.uit.socialjob.platform.modules.skill.repository.SkillCategoryRepository;
import vn.edu.uit.socialjob.platform.modules.skill.repository.SkillRepository;
import vn.edu.uit.socialjob.platform.modules.skill.repository.UserSkillRepository;
import vn.edu.uit.socialjob.platform.modules.user.entity.User;
import vn.edu.uit.socialjob.platform.modules.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillCategoryRepository skillCategoryRepository;

    @Mock
    private UserService userRepository;

    @Mock
    private UserSkillRepository userSkillRepository;

    @InjectMocks
    private SkillService skillService;

    @Test
    void createUserSkill_shouldRestoreSoftDeletedRecord_whenSameUserAndSkillExists() {
        UUID userId = UUID.randomUUID();
        UUID skillId = UUID.randomUUID();

        User user = new User();
        Skill skill = new Skill();
        CreateUserSkill request = new CreateUserSkill(skillId, 4);

        UserSkill softDeleted = new UserSkill();
        softDeleted.setDeleted(true);
        softDeleted.setLevel(1);

        when(userRepository.getById(userId)).thenReturn(user);
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(userSkillRepository.findByUserIdAndSkillId(userId, skillId)).thenReturn(Optional.of(softDeleted));
        when(userSkillRepository.save(any(UserSkill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserSkill result = skillService.createUserSkill(userId, request);

        ArgumentCaptor<UserSkill> captor = ArgumentCaptor.forClass(UserSkill.class);
        verify(userSkillRepository).save(captor.capture());
        UserSkill saved = captor.getValue();

        assertSame(softDeleted, saved);
        assertSame(user, saved.getUser());
        assertSame(skill, saved.getSkill());
        assertEquals(4, saved.getLevel());
        assertFalse(saved.isDeleted());
        assertTrue(saved == result);
    }

    @Test
    void createUserSkill_shouldCreateNewRecord_whenNoExistingPair() {
        UUID userId = UUID.randomUUID();
        UUID skillId = UUID.randomUUID();

        User user = new User();
        Skill skill = new Skill();
        CreateUserSkill request = new CreateUserSkill(skillId, 2);

        when(userRepository.getById(userId)).thenReturn(user);
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(userSkillRepository.findByUserIdAndSkillId(userId, skillId)).thenReturn(Optional.empty());
        when(userSkillRepository.save(any(UserSkill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserSkill result = skillService.createUserSkill(userId, request);

        ArgumentCaptor<UserSkill> captor = ArgumentCaptor.forClass(UserSkill.class);
        verify(userSkillRepository).save(captor.capture());
        verify(userSkillRepository, never()).findById(any());

        UserSkill saved = captor.getValue();
        assertSame(user, saved.getUser());
        assertSame(skill, saved.getSkill());
        assertEquals(2, saved.getLevel());
        assertFalse(saved.isDeleted());
        assertTrue(saved == result);
    }
}
