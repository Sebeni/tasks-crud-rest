package com.crud.tasks.trello.client;

import com.crud.tasks.domain.trello.CreatedTrelloCardDto;
import com.crud.tasks.domain.trello.TrelloBoardDto;
import com.crud.tasks.domain.trello.TrelloCardDto;
import com.crud.tasks.trello.config.TrelloConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class TrelloClientTest {
   
    @InjectMocks
    private TrelloClient trelloClient;
    
    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private TrelloConfig trelloConfig;
    
    
    @BeforeEach
    public void init() {
        when(trelloConfig.getTrelloApiEndpoint()).thenReturn("http://test.com");
        when(trelloConfig.getTrelloUsername()).thenReturn("test");
        when(trelloConfig.getTrelloAppKey()).thenReturn("test");
        when(trelloConfig.getTrelloToken()).thenReturn("test");
    }
    
    @Test
    public void shouldFetchTrelloBoards() throws URISyntaxException {
//        given
        TrelloBoardDto[] trelloBoards = new TrelloBoardDto[1];
        String inputName = "test_id";
        String inputId = "test_board";
        
        trelloBoards[0] = new TrelloBoardDto(inputId, inputName, new ArrayList<>());
        URI uri = new URI("http://test.com/members/test/boards?key=test&token=test&fields=name,id&lists=all");
        when(restTemplate.getForObject(uri, TrelloBoardDto[].class)).thenReturn(trelloBoards);
        
//        when
        List<TrelloBoardDto> fetchedTrelloBoards = trelloClient.getTrelloBoards();
//        then
        
        assertAll(
                () -> assertEquals(1, fetchedTrelloBoards.size()),
                () -> assertEquals(inputId, fetchedTrelloBoards.get(0).getId()),
                () -> assertEquals(inputName, fetchedTrelloBoards.get(0).getName()),
                () -> assertEquals(new ArrayList<>(), fetchedTrelloBoards.get(0).getLists())
        );
        
    }
    
    @Test
    public void shouldCreateCard() throws URISyntaxException {
//        given
        String cardName = "Test task";
        String cardDescription = "Test Description";
        String pos = "top";
        String listId = "test_id";
        
        TrelloCardDto trelloCardDto = new TrelloCardDto(cardName, cardDescription, pos, listId);
        
        URI uri = new URI("http://test.com/cards?key=test&token=test&name=Test%20task&desc=Test%20Description&pos=top&idList=test_id");
        CreatedTrelloCardDto createdTrelloCardDto = new CreatedTrelloCardDto("1", cardName, "http://test.com");
        
        when(restTemplate.postForObject(uri, null, CreatedTrelloCardDto.class)).thenReturn(createdTrelloCardDto);
        
//        when
        CreatedTrelloCardDto newCard = trelloClient.createNewCard(trelloCardDto);
        
        
//        then
        assertAll(
                () -> assertEquals("1", newCard.getId()),
                () -> assertEquals(cardName, newCard.getName()),
                () -> assertEquals("http://test.com", newCard.getShortUrl())
        );
    }

    @Test
    public void shouldReturnEmptyList() throws URISyntaxException {
//        given
        URI uri = new URI("http://test.com/members/test/boards?key=test&token=test&fields=name,id&lists=all");
        when(restTemplate.getForObject(uri, TrelloBoardDto[].class)).thenReturn(null);
        
//        when
        List<TrelloBoardDto> result = trelloClient.getTrelloBoards();
        
//        then
        assertAll(
                () -> assertEquals(0, result.size()),
                () -> assertEquals(new ArrayList<>(), result)
        );
    }
}