package com.yuce.chat.assistant.service.impl;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IntentExtractionResult;
import com.yuce.chat.assistant.persistence.repository.DrugRepository;
import com.yuce.chat.assistant.service.DrugService;
import com.yuce.chat.assistant.service.IntentService;
import com.yuce.chat.assistant.util.Constants;
import com.yuce.chat.assistant.util.HtmlTextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("drug-service")
@Slf4j
public class DrugServiceImpl implements DrugService, IntentService {

    @Autowired
    private DrugRepository drugRepository;

    @Override
    public Event run(IntentExtractionResult intent) {
        var findDrug = drugRepository.findByNameIgnoreCase(intent.getParameters().getDrugName());
        if (findDrug == null) {
            return new Event(Constants.DRUG, new EventResponse("No Drug found for " + intent.getParameters().getDrugName()));
        } else {
            return new Event(Constants.DRUG, new EventResponse(HtmlTextUtil.getInstance().formatDrugResponse(findDrug)));
        }
    }
}
