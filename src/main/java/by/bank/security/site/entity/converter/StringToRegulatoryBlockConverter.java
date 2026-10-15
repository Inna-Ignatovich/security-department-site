package by.bank.security.site.entity.converter;

import by.bank.security.site.entity.RegulatoryBlock;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@NullMarked
public class StringToRegulatoryBlockConverter implements Converter<String, RegulatoryBlock> {

    @Override
    public RegulatoryBlock convert(String source) {
        return RegulatoryBlock.fromValue(source);
    }
}