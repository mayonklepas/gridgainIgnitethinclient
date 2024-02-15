/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.msn.settlementmodule.settlementmodule;

import com.msn.settlementmodule.settlementmodule.entities.MasterAccountEntity;
import com.msn.settlementmodule.settlementmodule.entities.PACS002;
import com.msn.settlementmodule.settlementmodule.entities.PACS008;
import com.msn.settlementmodule.settlementmodule.entities.TransactionEntity;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.ignite.Ignite;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.cache.CacheWriteSynchronizationMode;
import org.apache.ignite.cache.PartitionLossPolicy;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DeploymentMode;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.failover.always.AlwaysFailoverSpi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author mulyadi
 */
@Configuration
public class AppConfiguration {

    @Value("${listAddress}")
    private String addresses;

    @Value("${ignite.port}")
    private String port;

    @Value("${ignite.pool.size}")
    private Integer poolSize;

    @Value("${ignite.rebalance.pool.size}")
    private Integer rebalancePoolSize;

    @Bean(name = "igniteInstance")
    public Ignite igniteInstance(Ignite ig) {
        ig.cluster().state(ClusterState.ACTIVE);
        return ig;
    }

    @Bean
    public IgniteConfiguration igniteConfiguration() {
        
        List<String> lsAddress = new ArrayList<>();

        if (addresses.contains(",")) {
            String[] arrayAddr = addresses.split(",");
            for (String item : arrayAddr) {
                lsAddress.add(item );
            }
        } else {
            addresses = addresses ;
            lsAddress.add(addresses);
        }
        
       

        IgniteConfiguration iCfg = new IgniteConfiguration();
        iCfg.setClientMode(true);
        iCfg.setPeerClassLoadingEnabled(true);
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(lsAddress);
        DiscoverySpi dSpi = new TcpDiscoverySpi().setIpFinder(ipFinder).setLocalPort(Integer.parseInt(port));
        iCfg.setDiscoverySpi(dSpi);
        AlwaysFailoverSpi failSpi = new AlwaysFailoverSpi();
        failSpi.setMaximumFailoverAttempts(5);
        iCfg.setFailoverSpi(failSpi);
        iCfg.setRebalanceThreadPoolSize(4);
        
        
         CacheConfiguration<String, MasterAccountEntity> masterAccountCacheConf = new CacheConfiguration<String, MasterAccountEntity>("masterAccountCache")
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setRebalanceMode(CacheRebalanceMode.ASYNC)
                .setCacheMode(CacheMode.PARTITIONED)
                .setBackups(3)
                .setPartitionLossPolicy(PartitionLossPolicy.READ_WRITE_SAFE)
                .setIndexedTypes(String.class, MasterAccountEntity.class)
                .setWriteSynchronizationMode(CacheWriteSynchronizationMode.PRIMARY_SYNC);

        CacheConfiguration<String, TransactionEntity> transactionAccountCacheConf = new CacheConfiguration<String, TransactionEntity>("transactionCache")
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setRebalanceMode(CacheRebalanceMode.ASYNC)
                .setCacheMode(CacheMode.PARTITIONED)
                .setBackups(3)
                .setPartitionLossPolicy(PartitionLossPolicy.READ_WRITE_SAFE)
                .setIndexedTypes(String.class, TransactionEntity.class)
                .setWriteSynchronizationMode(CacheWriteSynchronizationMode.PRIMARY_SYNC);
        
         CacheConfiguration<String, PACS008> pacs008CacheConf = new CacheConfiguration<String, PACS008>("pacs008Cache")
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setRebalanceMode(CacheRebalanceMode.ASYNC)
                .setCacheMode(CacheMode.PARTITIONED)
                .setBackups(3)
                .setPartitionLossPolicy(PartitionLossPolicy.READ_WRITE_SAFE)
                .setIndexedTypes(String.class, PACS008.class)
                .setWriteSynchronizationMode(CacheWriteSynchronizationMode.PRIMARY_SYNC);

        CacheConfiguration<String, PACS002> pacs002CacheConf = new CacheConfiguration<String, PACS002>("pacs002Cache")
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setRebalanceMode(CacheRebalanceMode.ASYNC)
                .setCacheMode(CacheMode.PARTITIONED)
                .setBackups(3)
                .setPartitionLossPolicy(PartitionLossPolicy.READ_WRITE_SAFE)
                .setIndexedTypes(String.class, PACS002.class)
                .setWriteSynchronizationMode(CacheWriteSynchronizationMode.PRIMARY_SYNC);
        
        iCfg.setCacheConfiguration(masterAccountCacheConf,transactionAccountCacheConf, pacs008CacheConf, pacs002CacheConf);

        return iCfg;
    }

    
    
}
