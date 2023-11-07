package com.example.phase_04.service;

import com.example.phase_04.baseService.BaseService;
import com.example.phase_04.entity.Order;
import com.example.phase_04.entity.TechnicianSuggestion;

import java.util.List;

public interface TechnicianSuggestionService extends BaseService<TechnicianSuggestion> {
    List<TechnicianSuggestion> getSuggestionsOrderedByPrice(Order order);
    List<TechnicianSuggestion> getSuggestionsOrderedByScore(Order order);
}
