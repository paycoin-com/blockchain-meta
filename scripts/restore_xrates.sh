#!/bin/bash

#### DEFINE COINS ##################

declare -a COINS=('BTC' 'ETH' 'BCH' 'DASH' 'ANKR' 'BAT' 'BNB' 'BNT' 'BTCB' 'CAS' 'CRO' 'CRPT' 'DAI' 'DGD' 'DGX' 'ELF' 'ENJ' 'EOS' 'EOSDT' 'EURS' 'GNT' 'GTO' 'GUSD' 'HOT' 'HT' 'IDXM' 'IQ' 'KCS' 'KNC' 'LINK' 'LOOM' 'LRC' 'MANA' 'MCO' 'MEETONE' 'MITH' 'MKR' 'NEXO' 'NPXS' 'OMG' 'ORBS' 'PAX' 'PGL' 'POLY' 'PPT' 'PTI' 'R' 'REP' 'SNT' 'TUSD' 'USDC' 'USDT' 'WAX' 'WTC' 'ZIL' 'ZRX')
declare -a FIATS=('USD' 'EUR' 'GBP' 'JPY' 'CAD' 'AUD' 'CNY' 'CHF' 'RUB' 'KRW' 'TRY')


#declare -a COINS=('BTC' 'BCH')
#declare -a FIATS=('USD' 'EUR')

####################################
BEX_API_URL="http://localhost:8181/bex/api/v1/currency/xrates/save"
####################################

if [ "$#" -ne 4 ]; then
    echo "Error! Illegal number of args."
    echo "Usage! YEAR-MONTH-DAY HOUR:MINUTE LIMIT (PERIOD)DAY/MINUTE"
    exit 1
fi

DATE=$1
TIME=$2
LIMIT=$3
PERIOD=$4
DO_ALL=false
CONTINUE=false

#############################################
function restoreValues () {
    
    echo "- $2"
    URL="$BEX_API_URL/$1/$2?to_date=$DATE%20$TIME&period=$PERIOD&limit=$LIMIT"
    
    curl $URL
     
}
#############################################

echo "Starting restoring data ..."
echo "For Date/Time: $DATE-$TIME Limit:$LIMIT Period:$PERIOD"
echo "--------------------"


for i in "${!COINS[@]}"; do
    
    
    if [ "$DO_ALL" = false ] ; then
        echo "Press (A)ll to restore all, (S)kip/(C)ontinue for ${COINS[$i]} or (Q)uit"
        read -s -n1
        echo ""

        if [[ $REPLY =~ ^[Aa]$ ]] ; then
            DO_ALL=true
        elif [[ $REPLY =~ ^[Cc]$ ]] ; then
            CONTINUE=true
        elif [[ $REPLY =~ ^[Qq]$ ]] ; then
            exit 1
        fi
    fi

    # -----------------------
    if [[ ("$DO_ALL" = true) || ("$CONTINUE" = true) ]] ; then
        echo "Restoring: " "${COINS[$i]}"
        CONTINUE=false
        for f in "${!FIATS[@]}"; do 
            restoreValues "${COINS[$i]}" "${FIATS[$f]}"
        done
    fi
done
