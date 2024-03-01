package com.bank.atm.repository;

import com.bank.atm.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

  @Query(value = "select * " +
      "from Token as t inner join bankUser as u " +
      "on t.user.id = u.id" +
      " where u.id = :id and (t.expired = false or t.revoked = false)", nativeQuery = true)
  List<Token> findAllValidTokenByUser(Integer id);

  Optional<Token> findByToken(String token);
}