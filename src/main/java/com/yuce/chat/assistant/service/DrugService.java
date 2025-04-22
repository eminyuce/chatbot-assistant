package com.yuce.chat.assistant.service;

import com.yuce.chat.assistant.model.Event;
import com.yuce.chat.assistant.model.EventResponse;
import com.yuce.chat.assistant.model.IntentResult;
import com.yuce.chat.assistant.persistence.repository.DrugRepository;
import com.yuce.chat.assistant.util.Constants;
import com.yuce.chat.assistant.util.FormatTextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DrugService {
    DrugRepository drugRepository;
    public Event getDrugInformation(IntentResult intent) {
        var findDrug = drugRepository.findByName(intent.getParameters().getDrugName());
        return new Event(Constants.DRUG, new EventResponse(FormatTextUtil.getInstance().formatDrugResponse(findDrug)));
    }
}
