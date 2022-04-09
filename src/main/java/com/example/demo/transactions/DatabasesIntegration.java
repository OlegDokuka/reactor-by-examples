package com.example.demo.transactions;

import java.time.Duration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class DatabasesIntegration {

  private final DatabaseApi oracleDb;
  private final DatabaseApi fileDb;

  public DatabasesIntegration(DatabaseApi oracleDb, DatabaseApi fileDb) {
    this.oracleDb = oracleDb;
    this.fileDb = fileDb;
  }

  public Mono<Void> storeToDatabases(String query) {

    return oracleDb.open().zipWith(fileDb.open())
        .flatMap(tupleOfTxs -> {
          final Transaction oracleTx = tupleOfTxs.getT1();
          final Transaction fileTx = tupleOfTxs.getT2();

          return Mono.zip(
              oracleTx.execute(query)
                  .then(Mono.fromSupplier(() -> Result.ok(oracleTx)))
                  .onErrorResume(t -> Mono.just(Result.error(t, oracleTx))),
              fileTx.execute(query)
                  .then(Mono.fromSupplier(() -> Result.ok(fileTx)))
                  .onErrorResume(t -> Mono.just(Result.error(t, fileTx)))
          );
        })
        .then();
//        .flatMap(tupleOfTxResults -> {
//          final Result oracleResult = tupleOfTxResuls.getT1();
//          final Result fileResult = tupleOfTxResuls.getT2();
//
//          if (oracleResult.error() != null || fileResult.error() != null) {
//            Mono.when(
//                oracleResult.session().rollback(),
//                fileResult.session().rollback()
//            ).then(Mono.error(new RuntimeException("transaction failed")));
//          }
//
//          return Mono.zip(
//              oracleResult.session().commit().then(Mono.just(true)).onErrorResume(t -> Mono.just(false)),
//              fileResult.session().commit().then(Mono.just(true)).onErrorResume(t -> Mono.just(false))
//          ).then();
//        });
  }


}
