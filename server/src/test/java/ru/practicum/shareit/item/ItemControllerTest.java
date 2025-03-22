package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithTime;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ItemServiceImpl itemService;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    void addItemTest() throws Exception {
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);

        when(itemService.addItem(any(Item.class), any(Long.class))).thenReturn(item);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void addCommentTest() throws Exception {
        Comment comment = new Comment();
        comment.setText("Test Comment");
        comment.setAuthorName("Author");

        when(itemService.addComment(any(Comment.class), any(Long.class), any(Long.class))).thenReturn(comment);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Test Comment"))
                .andExpect(jsonPath("$.authorName").value("Author"));
    }

    @Test
    void updateItemTest() throws Exception {
        Item item = new Item();
        item.setName("Updated Item");
        item.setDescription("Updated Description");
        item.setAvailable(false);

        when(itemService.updateItem(any(Item.class), any(Long.class), any(Long.class))).thenReturn(item);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void getItemTest() throws Exception {
        ItemDtoWithTime itemDtoWithTime = new ItemDtoWithTime();
        itemDtoWithTime.setId(1L);
        itemDtoWithTime.setName("Test Item");
        itemDtoWithTime.setDescription("Test Description");
        itemDtoWithTime.setAvailable(true);
        itemDtoWithTime.setLastBooking(LocalDateTime.now());
        itemDtoWithTime.setNextBooking(LocalDateTime.now().plusDays(1));
        itemDtoWithTime.setComments(Collections.emptyList());

        when(itemService.getItemWithTimeAndComments(any(Long.class), any(Long.class))).thenReturn(itemDtoWithTime);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.comments").isArray());
    }

    @Test
    void getAllItemsByOwnerTest() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        List<ItemDto> items = Collections.singletonList(itemDto);

        when(itemService.getAllItemsByOwner(any(Long.class))).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Item"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void searchItemTest() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        List<ItemDto> items = Collections.singletonList(itemDto);

        when(itemService.searchItems(any(Long.class), any(String.class))).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Item"))
                .andExpect(jsonPath("$[0].description").value("Test Description"))
                .andExpect(jsonPath("$[0].available").value(true));
    }
}