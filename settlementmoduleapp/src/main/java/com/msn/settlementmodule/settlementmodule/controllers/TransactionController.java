package com.msn.settlementmodule.settlementmodule.controllers;

import com.msn.settlementmodule.settlementmodule.entities.MasterAccountEntity;
import com.msn.settlementmodule.settlementmodule.entities.PACS002;
import com.msn.settlementmodule.settlementmodule.entities.PACS008;
import com.msn.settlementmodule.settlementmodule.entities.TransactionEntity;
import com.msn.settlementmodule.settlementmodule.utils.SttlProcess;
import java.util.HashMap;
import java.util.Map;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.affinity.Affinity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    public static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    Ignite ignite;

    @GetMapping("/init")
    public ResponseEntity<?> sendInit() {
        Map<String, Object> result = new HashMap<String, Object>();
        IgniteCache<Long, TransactionEntity> trxCache = ignite.cache("transactionCache");
        IgniteCache<String, MasterAccountEntity> masterAccountCache = ignite.cache("masterAccountCache");
        IgniteCache<String, PACS008> pacs008Cache = ignite.cache("pacs008Cache");
        IgniteCache<String, PACS002> pacs002Cache = ignite.cache("pacs002Cache");
        masterAccountCache.clear();
        trxCache.clear();
        pacs008Cache.clear();
        pacs002Cache.clear();
        masterAccountCache.put("CENAIDJA", new MasterAccountEntity("CENAIDJA", 1000_000.0, 0.0, 0.0, 0, 0, 0.0, 0.0, 0.0));
        masterAccountCache.put("BMRIIDJA", new MasterAccountEntity("BMRIIDJA", 1000_000.0, 0.0, 0.0, 0, 0, 0.0, 0.0, 0.0));
        result.put("systemStatus", "system on");
        result.put("cacheCreatedStatus", "Success");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/executesttl")
    public ResponseEntity<?> executeSttl(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<String, Object>();
        
        Affinity<String> affMaster = ignite.affinity("masterAccountCache");
        
      

        if (request.get("type").toString().equals("008")) {
            String bicPengirim = request.get("bicPengirim").toString();
            
            int computeResult = ignite.compute().affinityCall("masterAccountCache", bicPengirim,
                    new SttlProcess(request));
            request.put("code", computeResult);
        } else {
            IgniteCache<String, PACS008> pacs008Cache = ignite.cache("pacs008Cache");
            
            String eteid = request.get("endToEndId").toString();
            
            PACS008 pacs008Data = pacs008Cache.get(eteid);
            if (pacs008Data == null) {
                System.out.println("Pacs008 request tidak ditemukan");
                result.put("code", -1);
                return ResponseEntity.ok(result);
            }

            String bicPengirim = pacs008Data.getBicPengirim();
            String bicPenerima = pacs008Data.getBicPenerima();
            double amount = pacs008Data.getAmmount();

            request.put("bicPengirim", bicPengirim);
            request.put("bicPenerima", bicPenerima);
            request.put("amount", amount);

            int computeResult = ignite.compute().affinityCall("masterAccountCache", bicPengirim,
                    new SttlProcess(request));
            request.put("code", computeResult);
        }

        return ResponseEntity.ok(result);
    }

}
