//package me.qping.utils.database.util;
//
//import java.sql.SQLException;
//
///**
// * @ClassName TTT
// * @Description TODO
// * @Author qping
// * @Date 2021/1/15 09:35
// * @Version 1.0
// **/
//public class TTT {
//
//    @SuppressWarnings( "fallthrough" )
//    @Override
//    public ValueMetaInterface getValueFromSQLType( DatabaseMeta databaseMeta, String name, java.sql.ResultSetMetaData rm,
//                                                   int index, boolean ignoreLength, boolean lazyConversion ) throws KettleDatabaseException {
//        try {
//            int length = -1;
//            int precision = -1;
//            int valtype = ValueMetaInterface.TYPE_NONE;
//            boolean isClob = false;
//
//            int type = rm.getColumnType( index );
//            boolean signed = false;
//            try {
//                signed = rm.isSigned( index );
//            } catch ( Exception ignored ) {
//                // This JDBC Driver doesn't support the isSigned method
//                // nothing more we can do here by catch the exception.
//            }
//            switch ( type ) {
//                case java.sql.Types.CHAR:
//                case java.sql.Types.VARCHAR:
//                case java.sql.Types.NVARCHAR:
//                case java.sql.Types.LONGVARCHAR: // Character Large Object
//                    valtype = ValueMetaInterface.TYPE_STRING;
//                    if ( !ignoreLength ) {
//                        length = rm.getColumnDisplaySize( index );
//                    }
//                    break;
//
//                case java.sql.Types.CLOB:
//                case java.sql.Types.NCLOB:
//                    valtype = ValueMetaInterface.TYPE_STRING;
//                    length = DatabaseMeta.CLOB_LENGTH;
//                    isClob = true;
//                    break;
//
//                case java.sql.Types.BIGINT:
//                    // verify Unsigned BIGINT overflow!
//                    //
//                    if ( signed ) {
//                        valtype = ValueMetaInterface.TYPE_INTEGER;
//                        precision = 0; // Max 9.223.372.036.854.775.807
//                        length = 15;
//                    } else {
//                        valtype = ValueMetaInterface.TYPE_BIGNUMBER;
//                        precision = 0; // Max 18.446.744.073.709.551.615
//                        length = 16;
//                    }
//                    break;
//
//                case java.sql.Types.INTEGER:
//                    valtype = ValueMetaInterface.TYPE_INTEGER;
//                    precision = 0; // Max 2.147.483.647
//                    length = 9;
//                    break;
//
//                case java.sql.Types.SMALLINT:
//                    valtype = ValueMetaInterface.TYPE_INTEGER;
//                    precision = 0; // Max 32.767
//                    length = 4;
//                    break;
//
//                case java.sql.Types.TINYINT:
//                    valtype = ValueMetaInterface.TYPE_INTEGER;
//                    precision = 0; // Max 127
//                    length = 2;
//                    break;
//
//                case java.sql.Types.DECIMAL:
//                case java.sql.Types.DOUBLE:
//                case java.sql.Types.FLOAT:
//                case java.sql.Types.REAL:
//                case java.sql.Types.NUMERIC:
//                    valtype = ValueMetaInterface.TYPE_NUMBER;
//                    length = rm.getPrecision( index );
//                    precision = rm.getScale( index );
//                    if ( length >= 126 ) {
//                        length = -1;
//                    }
//                    if ( precision >= 126 ) {
//                        precision = -1;
//                    }
//
//                    if ( type == java.sql.Types.DOUBLE || type == java.sql.Types.FLOAT || type == java.sql.Types.REAL ) {
//                        if ( precision == 0 ) {
//                            precision = -1; // precision is obviously incorrect if the type if
//                            // Double/Float/Real
//                        }
//
//                        // If we're dealing with PostgreSQL and double precision types
//                        if ( databaseMeta.getDatabaseInterface() instanceof PostgreSQLDatabaseMeta && type == java.sql.Types.DOUBLE
//                                && precision >= 16 && length >= 16 ) {
//                            precision = -1;
//                            length = -1;
//                        }
//
//                        // MySQL: max resolution is double precision floating point (double)
//                        // The (12,31) that is given back is not correct
//                        if ( databaseMeta.getDatabaseInterface().isMySQLVariant() ) {
//                            if ( precision >= length ) {
//                                precision = -1;
//                                length = -1;
//                            }
//                        }
//
//                        // if the length or precision needs a BIGNUMBER
//                        if ( length > 15 || precision > 15 ) {
//                            valtype = ValueMetaInterface.TYPE_BIGNUMBER;
//                        }
//                    } else {
//                        if ( precision == 0 ) {
//                            if ( length <= 18 && length > 0 ) { // Among others Oracle is affected
//                                // here.
//                                valtype = ValueMetaInterface.TYPE_INTEGER; // Long can hold up to 18
//                                // significant digits
//                            } else if ( length > 18 ) {
//                                valtype = ValueMetaInterface.TYPE_BIGNUMBER;
//                            }
//                        } else { // we have a precision: keep NUMBER or change to BIGNUMBER?
//                            if ( length > 15 || precision > 15 ) {
//                                valtype = ValueMetaInterface.TYPE_BIGNUMBER;
//                            }
//                        }
//                    }
//
//                    if ( databaseMeta.getDatabaseInterface() instanceof PostgreSQLDatabaseMeta
//                            || databaseMeta.getDatabaseInterface() instanceof GreenplumDatabaseMeta ) {
//                        // undefined size => arbitrary precision
//                        if ( type == java.sql.Types.NUMERIC && length == 0 && precision == 0 ) {
//                            valtype = ValueMetaInterface.TYPE_BIGNUMBER;
//                            length = -1;
//                            precision = -1;
//                        }
//                    }
//
//                    if ( databaseMeta.getDatabaseInterface() instanceof OracleDatabaseMeta ) {
//                        if ( precision == 0 && length == 38 ) {
//                            valtype = ( (OracleDatabaseMeta) databaseMeta.getDatabaseInterface() )
//                                    .strictBigNumberInterpretation() ? TYPE_BIGNUMBER : TYPE_INTEGER;
//                        }
//                        if ( precision <= 0 && length <= 0 ) {
//                            // undefined size: BIGNUMBER,
//                            // precision on Oracle can be 38, too
//                            // big for a Number type
//                            valtype = ValueMetaInterface.TYPE_BIGNUMBER;
//                            length = -1;
//                            precision = -1;
//                        }
//                    }
//
//                    break;
//
//                case java.sql.Types.TIMESTAMP:
//                    if ( databaseMeta.supportsTimestampDataType() ) {
//                        valtype = ValueMetaInterface.TYPE_TIMESTAMP;
//                        length = rm.getScale( index );
//                    }
//                    break;
//
//                case java.sql.Types.DATE:
//                    if ( databaseMeta.getDatabaseInterface() instanceof TeradataDatabaseMeta ) {
//                        precision = 1;
//                    }
//                case java.sql.Types.TIME:
//                    valtype = ValueMetaInterface.TYPE_DATE;
//                    //
//                    if ( databaseMeta.getDatabaseInterface().isMySQLVariant() ) {
//                        String property = databaseMeta.getConnectionProperties().getProperty( "yearIsDateType" );
//                        if ( property != null && property.equalsIgnoreCase( "false" )
//                                && rm.getColumnTypeName( index ).equalsIgnoreCase( "YEAR" ) ) {
//                            valtype = ValueMetaInterface.TYPE_INTEGER;
//                            precision = 0;
//                            length = 4;
//                            break;
//                        }
//                    }
//                    break;
//
//                case java.sql.Types.BOOLEAN:
//                case java.sql.Types.BIT:
//                    valtype = ValueMetaInterface.TYPE_BOOLEAN;
//                    break;
//
//                case java.sql.Types.BINARY:
//                case java.sql.Types.BLOB:
//                case java.sql.Types.VARBINARY:
//                case java.sql.Types.LONGVARBINARY:
//                    valtype = ValueMetaInterface.TYPE_BINARY;
//
//                    if ( databaseMeta.isDisplaySizeTwiceThePrecision()
//                            && ( 2 * rm.getPrecision( index ) ) == rm.getColumnDisplaySize( index ) ) {
//                        // set the length for "CHAR(X) FOR BIT DATA"
//                        length = rm.getPrecision( index );
//                    } else if ( ( databaseMeta.getDatabaseInterface() instanceof OracleDatabaseMeta )
//                            && ( type == java.sql.Types.VARBINARY || type == java.sql.Types.LONGVARBINARY ) ) {
//                        // set the length for Oracle "RAW" or "LONGRAW" data types
//                        valtype = ValueMetaInterface.TYPE_STRING;
//                        length = rm.getColumnDisplaySize( index );
//                    } else if ( databaseMeta.isMySQLVariant()
//                            && ( type == java.sql.Types.VARBINARY || type == java.sql.Types.LONGVARBINARY ) ) {
//                        // PDI-6677 - don't call 'length = rm.getColumnDisplaySize(index);'
//                        length = -1; // keep the length to -1, e.g. for string functions (e.g.
//                        // CONCAT see PDI-4812)
//                    } else if ( databaseMeta.getDatabaseInterface() instanceof SQLiteDatabaseMeta ) {
//                        valtype = ValueMetaInterface.TYPE_STRING;
//                    } else {
//                        length = -1;
//                    }
//                    precision = -1;
//                    break;
//
//                default:
//                    valtype = ValueMetaInterface.TYPE_STRING;
//                    precision = rm.getScale( index );
//                    break;
//            }
//
//            ValueMetaInterface v = ValueMetaFactory.createValueMeta( name, valtype );
//            v.setLength( length );
//            v.setPrecision( precision );
//            v.setLargeTextField( isClob );
//
//            getOriginalColumnMetadata( v, rm, index, ignoreLength );
//
//            // See if we need to enable lazy conversion...
//            //
//            if ( lazyConversion && valtype == ValueMetaInterface.TYPE_STRING ) {
//                v.setStorageType( ValueMetaInterface.STORAGE_TYPE_BINARY_STRING );
//                // TODO set some encoding to go with this.
//
//                // Also set the storage metadata. a copy of the parent, set to String too.
//                //
//                try {
//                    ValueMetaInterface storageMetaData = ValueMetaFactory.cloneValueMeta( v, ValueMetaInterface.TYPE_STRING );
//                    storageMetaData.setStorageType( ValueMetaInterface.STORAGE_TYPE_NORMAL );
//                    v.setStorageMetadata( storageMetaData );
//                } catch ( Exception e ) {
//                    throw new SQLException( e );
//                }
//            }
//
//            ValueMetaInterface newV = null;
//            try {
//                newV = databaseMeta.getDatabaseInterface().customizeValueFromSQLType( v, rm, index );
//            } catch ( SQLException e ) {
//                throw new SQLException( e );
//            }
//            return newV == null ? v : newV;
//        } catch ( Exception e ) {
//            throw new KettleDatabaseException( "Error determining value metadata from SQL resultset metadata", e );
//        }
//    }
//}
