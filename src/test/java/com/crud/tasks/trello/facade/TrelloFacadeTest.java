package com.crud.tasks.trello.facade;

import com.crud.tasks.domain.trello.TrelloBoard;
import com.crud.tasks.domain.trello.TrelloBoardDto;
import com.crud.tasks.domain.trello.TrelloList;
import com.crud.tasks.domain.trello.TrelloListDto;
import com.crud.tasks.mapper.TrelloMapper;
import com.crud.tasks.service.TrelloService;
import com.crud.tasks.trello.validator.TrelloValidator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest
class TrelloFacadeTest {
    
    @InjectMocks
    private TrelloFacade trelloFacade;
    
    @Mock
    private TrelloService trelloService;
    
    @Mock
    private TrelloValidator trelloValidator;
    
    @Mock
    private TrelloMapper trelloMapper;
    
    @Test
    public void shouldFetchEmptyList() {
//        given
        List<TrelloListDto> trelloLists = new ArrayList<>();
        trelloLists.add(new TrelloListDto("1", "test_list", false));
        
        List<TrelloBoardDto> trelloBoards = new ArrayList<>();
        trelloBoards.add(new TrelloBoardDto("1", "test", trelloLists));
        
        List<TrelloList> mappedTrelloLists = new ArrayList<>();
        mappedTrelloLists.add(new TrelloList("1", "test_list", false));

        List<TrelloBoard> mappedTrelloBoards = new ArrayList<>();
        mappedTrelloBoards.add(new TrelloBoard("1", "test", mappedTrelloLists));
        
        when(trelloService.fetchTrelloBoards()).thenReturn(trelloBoards);
        when(trelloMapper.mapToBoardList(trelloBoards)).thenReturn(mappedTrelloBoards);
        when(trelloMapper.mapToBoardDtoList(anyList())).thenReturn(new ArrayList<>());
        when(trelloValidator.validateTrelloBoards(mappedTrelloBoards)).thenReturn(new ArrayList<>());
        
//        when
        List<TrelloBoardDto> trelloBoardDtos = trelloFacade.fetchTrelloBoards();
        
//        then
        assertAll(
                () -> assertNotNull(trelloBoardDtos),
                () -> assertEquals(0, trelloBoardDtos.size())
        );
    }
    
    @Test
    public void shouldFetchTrelloBoards() {
//        given
        List<TrelloListDto> trelloLists = new ArrayList<>();
        trelloLists.add(new TrelloListDto("1", "my_list", false));

        List<TrelloBoardDto> trelloBoards = new ArrayList<>();
        trelloBoards.add(new TrelloBoardDto("1", "my_task", trelloLists));

        List<TrelloList> mappedTrelloLists = new ArrayList<>();
        mappedTrelloLists.add(new TrelloList("1", "my_list", false));

        List<TrelloBoard> mappedTrelloBoards = new ArrayList<>();
        mappedTrelloBoards.add(new TrelloBoard("1", "my_task", mappedTrelloLists));

        when(trelloService.fetchTrelloBoards()).thenReturn(trelloBoards);
        when(trelloMapper.mapToBoardList(trelloBoards)).thenReturn(mappedTrelloBoards);
        when(trelloMapper.mapToBoardDtoList(anyList())).thenReturn(trelloBoards);
        when(trelloValidator.validateTrelloBoards(mappedTrelloBoards)).thenReturn(mappedTrelloBoards);
        
//        when
        List<TrelloBoardDto> trelloBoardDtos = trelloFacade.fetchTrelloBoards();
        
//        then
        assertNotNull(trelloBoardDtos);
        assertEquals(1, trelloBoardDtos.size());
        
        trelloBoardDtos.forEach(trelloBoardDto -> {
            assertEquals("1", trelloBoardDto.getId());
            assertEquals("my_task", trelloBoardDto.getName());
            
            trelloBoardDto.getLists().forEach(trelloListDto -> {
                assertEquals("1", trelloListDto.getId());
                assertEquals("my_list", trelloListDto.getName());
                assertEquals(false, trelloListDto.isClosed());
            });
        });
    }
}