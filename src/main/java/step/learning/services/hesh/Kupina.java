package step.learning.services.hesh;

public class Kupina {

    public Kupina( int bitLength ) {
        if( bitLength < 8 || bitLength > 1024 ) {
            throw new IllegalArgumentException( "Invalid bit length. Use 8..1024 values" ) ;
        }
        if( bitLength <= 256 ) {
            this.blockSize  = 512 ;
            this.columns    = 8 ;
            this.iterations = 10 ;
            this.shift[7]   = 7 ;
        }
        else {
            this.blockSize  = 1024 ;
            this.columns    = 16 ;
            this.iterations = 14 ;
            this.shift[7]   = 11 ;
        }
        this.hashSize = (short) bitLength ;

        this.state = new short[ this.columns ][] ;
        this.iv    = new short[ this.columns ][] ;
        for( int i = 0; i < this.columns; ++i ) {
            this.state[ i ] = new short[ 8 ] ;
            this.iv[ i ]    = new short[ 8 ] ;
        }
    }

    @Override
    public String toString() {
        return this.matrixToString( this.state ) ;
    }

    public void update( byte[] data ) {
        this.initState() ;
        for( short b : data ) {
            this.state[ this.lastCol ][ this.lastRow ] =
                    (short)( ( b < 0 ) ? b + 256 : b ) ;   // unsigned byte
            this.incLastCell() ;
            this.dataLength += 8 ;
        }
        this.state[ this.lastCol ][ this.lastRow ] = 0x80 ;
        this.incLastCell() ;
        this.writeSize() ;
    }

    public void update( String hexData ) throws IllegalArgumentException {
        this.initState() ;
        int y ;
        for( int pos = 0; pos < hexData.length(); ++pos ) {
            y = hexData.codePointAt( pos ) ;
            if( y >= 48 && y <= 57 )       y = y - 48 ;  // 0-9
            else if( y >= 65 && y <= 70 )  y = y - 55 ;  // A-F
            else if( y >= 97 && y <= 102 ) y = y - 87 ;  // a-f
            else throw new IllegalArgumentException(
                        "Invalid hexadecimal symbol: " + hexData.charAt( pos ) ) ;
            this.appendHalfByte( y ) ;
            this.dataLength += 4 ;
        }
        this.appendHalfByte( 8 ) ;
        this.writeSize() ;
    }

    public byte[] digest() {
        this.iv = this.hv() ;
        return this.rln() ;
    }

    public byte[] digest( byte[] data ) {
        this.update( data ) ;
        return this.digest() ;
    }

    public String digestHex() {
        StringBuilder sb = new StringBuilder() ;
        for( byte b : digest() ) {
            sb.append( String.format( "%02X", b ) ) ;
        }
        return sb.toString() ;
    }

    public String digestHex( byte[] data ) {
        this.update( data ) ;
        return this.digestHex() ;
    }

    public String digestHex( String hexData ) {
        this.update( hexData ) ;
        return this.digestHex() ;
    }

    ////////////////////////  PRIVATE /////////////////////////////////

    private short     hashSize   ;       // Hash (returned) size in bits (n)
    private short     blockSize  ;       // State matrix size in bits  (l)
    private short     columns    ;       // State matrix columns count (c)
    private short     iterations ;       // iteration count (t)
    private long      dataLength ;
    private short     lastCol    ;
    private short     lastRow    ;
    private boolean   isLastHigh ;
    private short[][] state      ;       // State matrix
    private short[][] iv         ;       // IV matrix

    // shift values for tau-transform
    private final short[] shift = { 0, 1, 2, 3, 4, 5, 6, 0 } ;


    private String matrixToString( short[][] matrix ) {
        StringBuilder sb = new StringBuilder() ;
        for( short[] col : matrix ) {
            for( short b : col ) {
                sb.append( String.format( "%02X", b ) ) ;
            }
        }
        return sb.toString() ;
    }

    private void appendHalfByte( int val ) {
        if( isLastHigh ) {
            state[ lastCol ][ lastRow ] = (short)( val << 4 ) ;
        }
        else {
            state[ lastCol ][ lastRow ] += val ;
            incLastCell() ;
        }
        isLastHigh = ! isLastHigh ;
    }

    private void incLastCell() {
        ++lastRow ;
        if( lastRow >= 8 ) {
            lastRow = 0;
            ++lastCol ;
            if( lastCol >= columns ) {
                iv = hv() ;
                for( int j = 0; j < columns; ++j )
                    for( int i = 0; i < 8; ++i ) {
                        state[ j ][ i ] = 0 ;
                    }
                lastCol = 0 ;
            }
        }
    }

    /**
     * Initial assignment
     */
    private void initState() {
        for( int j = 0; j < this.columns; ++j )
            for( int i = 0; i < 8; ++i ) {
                this.state[ j ][ i ] = 0 ;
                this.iv[ j ][ i ] = 0 ;
            }
        if( this.blockSize == 512 ) {
            this.iv[0][0] = (short) 0x40 ;
        }
        else {
            this.iv[0][0] = (short) 0x80 ;
        }
        lastCol = 0 ;
        lastRow = 0;
        isLastHigh = true ;
        dataLength = 0 ;
    }

    private short[][] hv() { return this.hv( this.state ) ; }

    private short[][] hv( short[][] m ) {
        short[][] h = new short[ this.columns ][] ;
        int i, j ;
        for( j = 0; j < this.columns; ++j ) {
            h[j] = new short[ 8 ] ;
            for( i = 0; i < 8; ++i ) {
                h[j][i] = (short)( this.iv[j][i] ^ m[j][i] ) ;
            }
        }
        short[][] t0 = this.transform0( h ) ;
        short[][] t1 = this.transform1() ;
        for( j = 0; j < this.columns; ++j ) {
            for( i = 0; i < 8; ++i ) {
                h[j][i] = (short)( t0[j][i] ^ t1[j][i] ^ this.iv[j][i] ) ;
            }
        }
        return h ;
    }

    private byte[] rln() {
        short[][] t0 = transform0( iv ) ;
        int i, j, k = 0 ;
        for( j = 0; j < columns; ++j ) {
            for( i = 0; i < 8; ++i ) {
                this.state[ j ][ i ] = (short)( t0[ j ][ i ] ^ iv[ j ][ i ] ) ;
            }
        }
        // return last I.b bit from I.G
        short si = (short)( ( blockSize - hashSize ) / 8 % 8 ) ;  // start i
        short sj = (short)( ( ( blockSize - hashSize ) - si * 8 ) / 8 / 8 ) ;  // start j
        byte[] ret = new byte[ hashSize / 8 ] ;
        for( j = sj; j < columns; ++j ) {
            for( i = (j==sj)?si:0; i < 8; ++i ) {
                ret[k] = (byte) state[ j ][ i ] ;
                ++k;
            }
        }
        return ret ;
    }

    private void writeSize() {
        if( freeBits() < 96 ) {
            iv = hv() ;
            for( int j = 0; j < columns; ++j )
                for( int i = 0; i < 8; ++i ) {
                    state[ j ][ i ] = 0 ;
                }
        }
        // 96 bit length little endian
        this.state[ this.columns - 2 ][ 4 ] = (short)( dataLength & 0xFF );
        this.state[ this.columns - 2 ][ 5 ] = (short)( ( dataLength >>> 8  ) & 0xFF ) ;
        this.state[ this.columns - 2 ][ 6 ] = (short)( ( dataLength >>> 16 ) & 0xFF ) ;
        this.state[ this.columns - 2 ][ 7 ] = (short)( ( dataLength >>> 24 ) & 0xFF ) ;
        this.state[ this.columns - 1 ][ 0 ] = (short)( ( dataLength >>> 32 ) & 0xFF ) ;
        this.state[ this.columns - 1 ][ 1 ] = (short)( ( dataLength >>> 40 ) & 0xFF ) ;
        this.state[ this.columns - 1 ][ 2 ] = (short)( ( dataLength >>> 48 ) & 0xFF ) ;
        this.state[ this.columns - 1 ][ 3 ] = (short)( ( dataLength >>> 56 ) & 0xFF ) ;
        this.state[ this.columns - 1 ][ 4 ] = 0 ;  // ( dataLength >>> 64 ) & 0xFF ;  // TODO: introduce int128 for length
        this.state[ this.columns - 1 ][ 5 ] = 0 ;  // ( dataLength >>> 72 ) & 0xFF ;
        this.state[ this.columns - 1 ][ 6 ] = 0 ;  // ( dataLength >>> 80 ) & 0xFF ;
        this.state[ this.columns - 1 ][ 7 ] = 0 ;  // ( dataLength >>> 88 ) & 0xFF ;
    }

    private short freeBits() {
        return (short)(
                ( ( columns - lastCol - 1 ) * 8 +
                        ( 8 - lastRow ) ) * 8 -
                        ( isLastHigh ? 0 : 4 ) ) ;
    }

    private short[][] transform0( short[][] matrix ) {
        short[][] Gtmp = new short[ this.columns ][] ;
        short[][] G = new short[ this.columns ][] ;
        short i, j ;

        for( j = 0; j < this.columns; ++j ) {
            Gtmp[ j ] = new short[ 8 ] ;
            G[ j ] = new short[ 8 ] ;
            for( i = 0; i < 8; ++i ) {
                Gtmp[ j ][ i ] = 0 ;
                G[ j ][ i ] = matrix[ j ][ i ] ;
            }
        }

        for( short n = 0; n < this.iterations; ++n ) {
            for( j = 0; j < this.columns; ++j ) {
                G[ j ][ 0 ] ^= ( j << 4 ) ^ n ;  // kappa
                for( i = 0; i < 8; ++i ) {
                    Gtmp[ ( j + this.shift[ i ] ) & ( this.columns - 1 ) ][ i ] =
                            Kupina.sbox[ i ][ G[ j ][ i ] ] ;
                }
            }
            // psi
            for( j = 0; j < this.columns; ++j ) {
                for( i = 0; i < 8; ++i ) {
                    short acc = 0 ;
                    for( short k = 0; k < 8; ++k ) {
                        acc ^= this.gfMul(
                                Kupina.v[ ( k - i + 8 ) & 7 ],
                                Gtmp[ j ][ k ] ) ;
                    }
                    G[ j ][ i ] = (short) ( acc & 0xFF ) ;
                }
            }
        }
        return G ;
    }

    private short[][] transform1( ) { return transform1( this.state ) ; }

    private short[][] transform1( short[][] matrix ) {
        short[][] Gtmp = new short[ this.columns ][] ;
        short[][] G = new short[ this.columns ][] ;
        short i, j ;

        for( j = 0; j < this.columns; ++j ) {
            Gtmp[ j ] = new short[ 8 ] ;
            G[ j ] = new short[ 8 ] ;
            for( i = 0; i < 8; ++i ) {
                Gtmp[ j ][ i ] = 0 ;
                G[ j ][ i ] = matrix[ j ][ i ] ;
            }
        }

        for( short n = 0; n < this.iterations; ++n ) {
            for( j = 0; j < this.columns; ++j ) {
                short[] dz = { 243, 240, 240, 240, 240, 240, 240, (short)( ( ( ( this.columns - 1 - j ) << 4 ) ^ n ) & 0xFF ) } ;
                short carry_over = 0 ;
                for( i = 0; i < 8; ++i ) {
                    int x = G[ j ][ i ] + dz[ i ] + carry_over ;
                    G[ j ][ i ] = (short)( x & 0xFF ) ;
                    carry_over = (short)( ( x > 0xFF ) ? 1 : 0 ) ;
                }
            }
            for( j = 0; j < this.columns; ++j ) {
                for( i = 0; i < 8; ++i ) {
                    Gtmp[ ( j + this.shift[ i ] ) & ( this.columns - 1 ) ][ i ] = Kupina.sbox[ i ][ G[ j ][ i ] ] ;
                }
            }
            // psi
            for( j = 0; j < this.columns; ++j ) {
                for( i = 0; i < 8; ++i ) {
                    short acc = 0 ;
                    for( short k = 0; k < 8; ++k ) {
                        acc ^= this.gfMul(
                                Kupina.v[ ( k - i + 8 ) & 7 ],
                                Gtmp[ j ][ k ] ) ;
                    }
                    G[ j ][ i ] = (short)( acc & 0xFF ) ;
                }
            }
        }
        return G ;
    }

    /**
     * Multiplication in GF
     * @param a multiplier
     * @param b multiplier
     * @return a x b (mod GF)
     */
    private short gfMul( short a, short b ) {
        if( a == 0 || b == 0 ) {
            return 0 ;
        }
        return Kupina.pw2val[ Kupina.pw2ind[ a ] +  Kupina.pw2ind[ b ] ] ;
    }

    // data for linear transformation (psi)
    private static final short[] v = { 0x01, 0x01, 0x05, 0x01, 0x08, 0x06, 0x07, 0x04 } ;

    // S-boxes array
    private static final short[][] _sbox = {
            { 0xA8, 0x43, 0x5F, 0x06, 0x6B, 0x75, 0x6C, 0x59, 0x71, 0xDF, 0x87, 0x95, 0x17, 0xF0, 0xD8, 0x09, 0x6D, 0xF3, 0x1D, 0xCB, 0xC9, 0x4D, 0x2C, 0xAF, 0x79, 0xE0, 0x97, 0xFD, 0x6F, 0x4B, 0x45, 0x39, 0x3E, 0xDD, 0xA3, 0x4F, 0xB4, 0xB6, 0x9A, 0x0E, 0x1F, 0xBF, 0x15, 0xE1, 0x49, 0xD2, 0x93, 0xC6, 0x92, 0x72, 0x9E, 0x61, 0xD1, 0x63, 0xFA, 0xEE, 0xF4, 0x19, 0xD5, 0xAD, 0x58, 0xA4, 0xBB, 0xA1, 0xDC, 0xF2, 0x83, 0x37, 0x42, 0xE4, 0x7A, 0x32, 0x9C, 0xCC, 0xAB, 0x4A, 0x8F, 0x6E, 0x04, 0x27, 0x2E, 0xE7, 0xE2, 0x5A, 0x96, 0x16, 0x23, 0x2B, 0xC2, 0x65, 0x66, 0x0F, 0xBC, 0xA9, 0x47, 0x41, 0x34, 0x48, 0xFC, 0xB7, 0x6A, 0x88, 0xA5, 0x53, 0x86, 0xF9, 0x5B, 0xDB, 0x38, 0x7B, 0xC3, 0x1E, 0x22, 0x33, 0x24, 0x28, 0x36, 0xC7, 0xB2, 0x3B, 0x8E, 0x77, 0xBA, 0xF5, 0x14, 0x9F, 0x08, 0x55, 0x9B, 0x4C, 0xFE, 0x60, 0x5C, 0xDA, 0x18, 0x46, 0xCD, 0x7D, 0x21, 0xB0, 0x3F, 0x1B, 0x89, 0xFF, 0xEB, 0x84, 0x69, 0x3A, 0x9D, 0xD7, 0xD3, 0x70, 0x67, 0x40, 0xB5, 0xDE, 0x5D, 0x30, 0x91, 0xB1, 0x78, 0x11, 0x01, 0xE5, 0x00, 0x68, 0x98, 0xA0, 0xC5, 0x02, 0xA6, 0x74, 0x2D, 0x0B, 0xA2, 0x76, 0xB3, 0xBE, 0xCE, 0xBD, 0xAE, 0xE9, 0x8A, 0x31, 0x1C, 0xEC, 0xF1, 0x99, 0x94, 0xAA, 0xF6, 0x26, 0x2F, 0xEF, 0xE8, 0x8C, 0x35, 0x03, 0xD4, 0x7F, 0xFB, 0x05, 0xC1, 0x5E, 0x90, 0x20, 0x3D, 0x82, 0xF7, 0xEA, 0x0A, 0x0D, 0x7E, 0xF8, 0x50, 0x1A, 0xC4, 0x07, 0x57, 0xB8, 0x3C, 0x62, 0xE3, 0xC8, 0xAC, 0x52, 0x64, 0x10, 0xD0, 0xD9, 0x13, 0x0C, 0x12, 0x29, 0x51, 0xB9, 0xCF, 0xD6, 0x73, 0x8D, 0x81, 0x54, 0xC0, 0xED, 0x4E, 0x44, 0xA7, 0x2A, 0x85, 0x25, 0xE6, 0xCA, 0x7C, 0x8B, 0x56, 0x80 },
            { 0xCE, 0xBB, 0xEB, 0x92, 0xEA, 0xCB, 0x13, 0xC1, 0xE9, 0x3A, 0xD6, 0xB2, 0xD2, 0x90, 0x17, 0xF8, 0x42, 0x15, 0x56, 0xB4, 0x65, 0x1C, 0x88, 0x43, 0xC5, 0x5C, 0x36, 0xBA, 0xF5, 0x57, 0x67, 0x8D, 0x31, 0xF6, 0x64, 0x58, 0x9E, 0xF4, 0x22, 0xAA, 0x75, 0x0F, 0x02, 0xB1, 0xDF, 0x6D, 0x73, 0x4D, 0x7C, 0x26, 0x2E, 0xF7, 0x08, 0x5D, 0x44, 0x3E, 0x9F, 0x14, 0xC8, 0xAE, 0x54, 0x10, 0xD8, 0xBC, 0x1A, 0x6B, 0x69, 0xF3, 0xBD, 0x33, 0xAB, 0xFA, 0xD1, 0x9B, 0x68, 0x4E, 0x16, 0x95, 0x91, 0xEE, 0x4C, 0x63, 0x8E, 0x5B, 0xCC, 0x3C, 0x19, 0xA1, 0x81, 0x49, 0x7B, 0xD9, 0x6F, 0x37, 0x60, 0xCA, 0xE7, 0x2B, 0x48, 0xFD, 0x96, 0x45, 0xFC, 0x41, 0x12, 0x0D, 0x79, 0xE5, 0x89, 0x8C, 0xE3, 0x20, 0x30, 0xDC, 0xB7, 0x6C, 0x4A, 0xB5, 0x3F, 0x97, 0xD4, 0x62, 0x2D, 0x06, 0xA4, 0xA5, 0x83, 0x5F, 0x2A, 0xDA, 0xC9, 0x00, 0x7E, 0xA2, 0x55, 0xBF, 0x11, 0xD5, 0x9C, 0xCF, 0x0E, 0x0A, 0x3D, 0x51, 0x7D, 0x93, 0x1B, 0xFE, 0xC4, 0x47, 0x09, 0x86, 0x0B, 0x8F, 0x9D, 0x6A, 0x07, 0xB9, 0xB0, 0x98, 0x18, 0x32, 0x71, 0x4B, 0xEF, 0x3B, 0x70, 0xA0, 0xE4, 0x40, 0xFF, 0xC3, 0xA9, 0xE6, 0x78, 0xF9, 0x8B, 0x46, 0x80, 0x1E, 0x38, 0xE1, 0xB8, 0xA8, 0xE0, 0x0C, 0x23, 0x76, 0x1D, 0x25, 0x24, 0x05, 0xF1, 0x6E, 0x94, 0x28, 0x9A, 0x84, 0xE8, 0xA3, 0x4F, 0x77, 0xD3, 0x85, 0xE2, 0x52, 0xF2, 0x82, 0x50, 0x7A, 0x2F, 0x74, 0x53, 0xB3, 0x61, 0xAF, 0x39, 0x35, 0xDE, 0xCD, 0x1F, 0x99, 0xAC, 0xAD, 0x72, 0x2C, 0xDD, 0xD0, 0x87, 0xBE, 0x5E, 0xA6, 0xEC, 0x04, 0xC6, 0x03, 0x34, 0xFB, 0xDB, 0x59, 0xB6, 0xC2, 0x01, 0xF0, 0x5A, 0xED, 0xA7, 0x66, 0x21, 0x7F, 0x8A, 0x27, 0xC7, 0xC0, 0x29, 0xD7 },
            { 0x93, 0xD9, 0x9A, 0xB5, 0x98, 0x22, 0x45, 0xFC, 0xBA, 0x6A, 0xDF, 0x02, 0x9F, 0xDC, 0x51, 0x59, 0x4A, 0x17, 0x2B, 0xC2, 0x94, 0xF4, 0xBB, 0xA3, 0x62, 0xE4, 0x71, 0xD4, 0xCD, 0x70, 0x16, 0xE1, 0x49, 0x3C, 0xC0, 0xD8, 0x5C, 0x9B, 0xAD, 0x85, 0x53, 0xA1, 0x7A, 0xC8, 0x2D, 0xE0, 0xD1, 0x72, 0xA6, 0x2C, 0xC4, 0xE3, 0x76, 0x78, 0xB7, 0xB4, 0x09, 0x3B, 0x0E, 0x41, 0x4C, 0xDE, 0xB2, 0x90, 0x25, 0xA5, 0xD7, 0x03, 0x11, 0x00, 0xC3, 0x2E, 0x92, 0xEF, 0x4E, 0x12, 0x9D, 0x7D, 0xCB, 0x35, 0x10, 0xD5, 0x4F, 0x9E, 0x4D, 0xA9, 0x55, 0xC6, 0xD0, 0x7B, 0x18, 0x97, 0xD3, 0x36, 0xE6, 0x48, 0x56, 0x81, 0x8F, 0x77, 0xCC, 0x9C, 0xB9, 0xE2, 0xAC, 0xB8, 0x2F, 0x15, 0xA4, 0x7C, 0xDA, 0x38, 0x1E, 0x0B, 0x05, 0xD6, 0x14, 0x6E, 0x6C, 0x7E, 0x66, 0xFD, 0xB1, 0xE5, 0x60, 0xAF, 0x5E, 0x33, 0x87, 0xC9, 0xF0, 0x5D, 0x6D, 0x3F, 0x88, 0x8D, 0xC7, 0xF7, 0x1D, 0xE9, 0xEC, 0xED, 0x80, 0x29, 0x27, 0xCF, 0x99, 0xA8, 0x50, 0x0F, 0x37, 0x24, 0x28, 0x30, 0x95, 0xD2, 0x3E, 0x5B, 0x40, 0x83, 0xB3, 0x69, 0x57, 0x1F, 0x07, 0x1C, 0x8A, 0xBC, 0x20, 0xEB, 0xCE, 0x8E, 0xAB, 0xEE, 0x31, 0xA2, 0x73, 0xF9, 0xCA, 0x3A, 0x1A, 0xFB, 0x0D, 0xC1, 0xFE, 0xFA, 0xF2, 0x6F, 0xBD, 0x96, 0xDD, 0x43, 0x52, 0xB6, 0x08, 0xF3, 0xAE, 0xBE, 0x19, 0x89, 0x32, 0x26, 0xB0, 0xEA, 0x4B, 0x64, 0x84, 0x82, 0x6B, 0xF5, 0x79, 0xBF, 0x01, 0x5F, 0x75, 0x63, 0x1B, 0x23, 0x3D, 0x68, 0x2A, 0x65, 0xE8, 0x91, 0xF6, 0xFF, 0x13, 0x58, 0xF1, 0x47, 0x0A, 0x7F, 0xC5, 0xA7, 0xE7, 0x61, 0x5A, 0x06, 0x46, 0x44, 0x42, 0x04, 0xA0, 0xDB, 0x39, 0x86, 0x54, 0xAA, 0x8C, 0x34, 0x21, 0x8B, 0xF8, 0x0C, 0x74, 0x67 },
            { 0x68, 0x8D, 0xCA, 0x4D, 0x73, 0x4B, 0x4E, 0x2A, 0xD4, 0x52, 0x26, 0xB3, 0x54, 0x1E, 0x19, 0x1F, 0x22, 0x03, 0x46, 0x3D, 0x2D, 0x4A, 0x53, 0x83, 0x13, 0x8A, 0xB7, 0xD5, 0x25, 0x79, 0xF5, 0xBD, 0x58, 0x2F, 0x0D, 0x02, 0xED, 0x51, 0x9E, 0x11, 0xF2, 0x3E, 0x55, 0x5E, 0xD1, 0x16, 0x3C, 0x66, 0x70, 0x5D, 0xF3, 0x45, 0x40, 0xCC, 0xE8, 0x94, 0x56, 0x08, 0xCE, 0x1A, 0x3A, 0xD2, 0xE1, 0xDF, 0xB5, 0x38, 0x6E, 0x0E, 0xE5, 0xF4, 0xF9, 0x86, 0xE9, 0x4F, 0xD6, 0x85, 0x23, 0xCF, 0x32, 0x99, 0x31, 0x14, 0xAE, 0xEE, 0xC8, 0x48, 0xD3, 0x30, 0xA1, 0x92, 0x41, 0xB1, 0x18, 0xC4, 0x2C, 0x71, 0x72, 0x44, 0x15, 0xFD, 0x37, 0xBE, 0x5F, 0xAA, 0x9B, 0x88, 0xD8, 0xAB, 0x89, 0x9C, 0xFA, 0x60, 0xEA, 0xBC, 0x62, 0x0C, 0x24, 0xA6, 0xA8, 0xEC, 0x67, 0x20, 0xDB, 0x7C, 0x28, 0xDD, 0xAC, 0x5B, 0x34, 0x7E, 0x10, 0xF1, 0x7B, 0x8F, 0x63, 0xA0, 0x05, 0x9A, 0x43, 0x77, 0x21, 0xBF, 0x27, 0x09, 0xC3, 0x9F, 0xB6, 0xD7, 0x29, 0xC2, 0xEB, 0xC0, 0xA4, 0x8B, 0x8C, 0x1D, 0xFB, 0xFF, 0xC1, 0xB2, 0x97, 0x2E, 0xF8, 0x65, 0xF6, 0x75, 0x07, 0x04, 0x49, 0x33, 0xE4, 0xD9, 0xB9, 0xD0, 0x42, 0xC7, 0x6C, 0x90, 0x00, 0x8E, 0x6F, 0x50, 0x01, 0xC5, 0xDA, 0x47, 0x3F, 0xCD, 0x69, 0xA2, 0xE2, 0x7A, 0xA7, 0xC6, 0x93, 0x0F, 0x0A, 0x06, 0xE6, 0x2B, 0x96, 0xA3, 0x1C, 0xAF, 0x6A, 0x12, 0x84, 0x39, 0xE7, 0xB0, 0x82, 0xF7, 0xFE, 0x9D, 0x87, 0x5C, 0x81, 0x35, 0xDE, 0xB4, 0xA5, 0xFC, 0x80, 0xEF, 0xCB, 0xBB, 0x6B, 0x76, 0xBA, 0x5A, 0x7D, 0x78, 0x0B, 0x95, 0xE3, 0xAD, 0x74, 0x98, 0x3B, 0x36, 0x64, 0x6D, 0xDC, 0xF0, 0x59, 0xA9, 0x4C, 0x17, 0x7F, 0x91, 0xB8, 0xC9, 0x57, 0x1B, 0xE0, 0x61 }
    } ;
    // indexing repeated S-boxes
    private static final short[][] sbox = { _sbox[0], _sbox[1], _sbox[2], _sbox[3], _sbox[0], _sbox[1], _sbox[2], _sbox[3] } ;

    // 2^x in GF - values and indexes (for fast multiplication in GF)
    private static final short[] pw2val = { 1, 2, 4, 8, 16, 32, 64, 128, 29, 58, 116, 232, 205, 135, 19, 38, 76, 152, 45, 90, 180, 117, 234, 201, 143, 3, 6, 12, 24, 48, 96, 192, 157, 39, 78, 156, 37, 74, 148, 53, 106, 212, 181, 119, 238, 193, 159, 35, 70, 140, 5, 10, 20, 40, 80, 160, 93, 186, 105, 210, 185, 111, 222, 161, 95, 190, 97, 194, 153, 47, 94, 188, 101, 202, 137, 15, 30, 60, 120, 240, 253, 231, 211, 187, 107, 214, 177, 127, 254, 225, 223, 163, 91, 182, 113, 226, 217, 175, 67, 134, 17, 34, 68, 136, 13, 26, 52, 104, 208, 189, 103, 206, 129, 31, 62, 124, 248, 237, 199, 147, 59, 118, 236, 197, 151, 51, 102, 204, 133, 23, 46, 92, 184, 109, 218, 169, 79, 158, 33, 66, 132, 21, 42, 84, 168, 77, 154, 41, 82, 164, 85, 170, 73, 146, 57, 114, 228, 213, 183, 115, 230, 209, 191, 99, 198, 145, 63, 126, 252, 229, 215, 179, 123, 246, 241, 255, 227, 219, 171, 75, 150, 49, 98, 196, 149, 55, 110, 220, 165, 87, 174, 65, 130, 25, 50, 100, 200, 141, 7, 14, 28, 56, 112, 224, 221, 167, 83, 166, 81, 162, 89, 178, 121, 242, 249, 239, 195, 155, 43, 86, 172, 69, 138, 9, 18, 36, 72, 144, 61, 122, 244, 245, 247, 243, 251, 235, 203, 139, 11, 22, 44, 88, 176, 125, 250, 233, 207, 131, 27, 54, 108, 216, 173, 71, 142, 1, 2, 4, 8, 16, 32, 64, 128, 29, 58, 116, 232, 205, 135, 19, 38, 76, 152, 45, 90, 180, 117, 234, 201, 143, 3, 6, 12, 24, 48, 96, 192, 157, 39, 78, 156, 37, 74, 148, 53, 106, 212, 181, 119, 238, 193, 159, 35, 70, 140, 5, 10, 20, 40, 80, 160, 93, 186, 105, 210, 185, 111, 222, 161, 95, 190, 97, 194, 153, 47, 94, 188, 101, 202, 137, 15, 30, 60, 120, 240, 253, 231, 211, 187, 107, 214, 177, 127, 254, 225, 223, 163, 91, 182, 113, 226, 217, 175, 67, 134, 17, 34, 68, 136, 13, 26, 52, 104, 208, 189, 103, 206, 129, 31, 62, 124, 248, 237, 199, 147, 59, 118, 236, 197, 151, 51, 102, 204, 133, 23, 46, 92, 184, 109, 218, 169, 79, 158, 33, 66, 132, 21, 42, 84, 168, 77, 154, 41, 82, 164, 85, 170, 73, 146, 57, 114, 228, 213, 183, 115, 230, 209, 191, 99, 198, 145, 63, 126, 252, 229, 215, 179, 123, 246, 241, 255, 227, 219, 171, 75, 150, 49, 98, 196, 149, 55, 110, 220, 165, 87, 174, 65, 130, 25, 50, 100, 200, 141, 7, 14, 28, 56, 112, 224, 221, 167, 83, 166, 81, 162, 89, 178, 121, 242, 249, 239, 195, 155, 43, 86, 172, 69, 138, 9, 18, 36, 72, 144, 61, 122, 244, 245, 247, 243, 251, 235, 203, 139, 11, 22, 44, 88, 176, 125, 250, 233, 207, 131, 27, 54, 108, 216, 173, 71, 142, 1 } ;
    private static final short[] pw2ind = { 0, 0, 1, 25, 2, 50, 26, 198, 3, 223, 51, 238, 27, 104, 199, 75, 4, 100, 224, 14, 52, 141, 239, 129, 28, 193, 105, 248, 200, 8, 76, 113, 5, 138, 101, 47, 225, 36, 15, 33, 53, 147, 142, 218, 240, 18, 130, 69, 29, 181, 194, 125, 106, 39, 249, 185, 201, 154, 9, 120, 77, 228, 114, 166, 6, 191, 139, 98, 102, 221, 48, 253, 226, 152, 37, 179, 16, 145, 34, 136, 54, 208, 148, 206, 143, 150, 219, 189, 241, 210, 19, 92, 131, 56, 70, 64, 30, 66, 182, 163, 195, 72, 126, 110, 107, 58, 40, 84, 250, 133, 186, 61, 202, 94, 155, 159, 10, 21, 121, 43, 78, 212, 229, 172, 115, 243, 167, 87, 7, 112, 192, 247, 140, 128, 99, 13, 103, 74, 222, 237, 49, 197, 254, 24, 227, 165, 153, 119, 38, 184, 180, 124, 17, 68, 146, 217, 35, 32, 137, 46, 55, 63, 209, 91, 149, 188, 207, 205, 144, 135, 151, 178, 220, 252, 190, 97, 242, 86, 211, 171, 20, 42, 93, 158, 132, 60, 57, 83, 71, 109, 65, 162, 31, 45, 67, 216, 183, 123, 164, 118, 196, 23, 73, 236, 127, 12, 111, 246, 108, 161, 59, 82, 41, 157, 85, 170, 251, 96, 134, 177, 187, 204, 62, 90, 203, 89, 95, 176, 156, 169, 160, 81, 11, 245, 22, 235, 122, 117, 44, 215, 79, 174, 213, 233, 230, 231, 173, 232, 116, 214, 244, 234, 168, 80, 88, 175 } ;



}
