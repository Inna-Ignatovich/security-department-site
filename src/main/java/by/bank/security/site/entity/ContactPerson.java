package by.bank.security.site.entity;

public record ContactPerson(String id,
                            String fullName,
                            String position,
                            boolean isLeader,
                            String phone,
                            String personCode,
                            String office,
                            String address) {
}