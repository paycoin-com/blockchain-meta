package io.hs.bex.blocknode.handler.bitcoin.model;

import java.util.EnumSet;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.utils.VersionTally;

public abstract class NetworkParams extends AbstractBitcoinNetParams
{
    
    @Override
    public EnumSet<Script.VerifyFlag> getTransactionVerificationFlags(final Block block,
            final Transaction transaction, final VersionTally tally, final Integer height) 
    {
        final EnumSet<Script.VerifyFlag> verifyFlags = EnumSet.noneOf(Script.VerifyFlag.class);
        if (block.getTimeSeconds() >= NetworkParameters.BIP16_ENFORCE_TIME)
            verifyFlags.add(Script.VerifyFlag.P2SH);

        // Start enforcing CHECKLOCKTIMEVERIFY, (BIP65) for block.nVersion=4
        // blocks, when 75% of the network has upgraded:
            Integer tallyCount = tally.getCountAtOrAbove(Block.BLOCK_VERSION_BIP65);
            tallyCount = (tallyCount == null)?  0 : tallyCount;
        if (block.getVersion() >= Block.BLOCK_VERSION_BIP65 && 
            tallyCount > this.getMajorityEnforceBlockUpgrade()) {
            verifyFlags.add(Script.VerifyFlag.CHECKLOCKTIMEVERIFY);
        }

        return verifyFlags;
    }
 
}
